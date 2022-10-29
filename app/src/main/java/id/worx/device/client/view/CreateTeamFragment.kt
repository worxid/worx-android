package id.worx.device.client.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.NewTeamForm
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.welcome.CreateTeamEvent
import id.worx.device.client.screen.welcome.CreateTeamScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModel
import id.worx.device.client.viewmodel.WelcomeViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()

    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.CreateTeam)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    CreateTeamScreen(
                        session = session,
                        onNavigationEvent = { event ->
                            when (event) {
                                is CreateTeamEvent.CreateTeam -> {
                                    session.saveOrganization(event.organizationName)
                                    viewModel.createTeam(
                                        onError = {
                                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        },
                                        newTeamForm = NewTeamForm(
                                            country = getCountryName(session, context),
                                            email = event.email,
                                            fullname = event.fullName,
                                            organization_name = event.organizationName,
                                            password = event.password,
                                            phoneNo = event.workPhone
                                        )
                                    )
                                }
                                CreateTeamEvent.NavigateBack -> {
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

fun getCountryName(session: Session, context: Context): String? {
    val geoCoder = Geocoder(context, Locale.getDefault())
    var address: MutableList<Address>? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geoCoder.getFromLocation(
            session.latitude!!.replace(",", ".").toDouble(),
            session.longitude!!.replace(",", ".").toDouble(),
            1
        ) { addresses -> address = addresses }
        return address?.getOrNull(0)?.countryName
    } else {
        try {
            if (!session.latitude.isNullOrEmpty() && !session.longitude.isNullOrEmpty()) {
                address = geoCoder.getFromLocation(
                    session.latitude!!.replace(",", ".").toDouble(),
                    session.longitude!!.replace(",", ".").toDouble(),
                    1
                )
                if (address != null) {
                    return address!!.getOrNull(0)!!.countryName
                }
            }
            return null
        } catch (e: Exception) {
            Log.d("TAG", "getCountryName: $e")
        }
    }
    return null
}