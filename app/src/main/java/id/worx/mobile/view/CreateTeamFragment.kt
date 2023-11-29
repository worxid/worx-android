package id.worx.mobile.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
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
import id.worx.mobile.WelcomeScreen
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.NewTeamForm
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.welcome.CreateTeamEvent
import id.worx.mobile.screen.welcome.CreateTeamScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import id.worx.mobile.viewmodel.WelcomeViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                                    session.saveDeviceName(event.fullName)
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

    try {
        if (!session.latitude.isNullOrEmpty() && !session.longitude.isNullOrEmpty()) {
            address = geoCoder.getFromLocation(
                session.latitude!!.replace(",", ".").toDouble(),
                session.longitude!!.replace(",", ".").toDouble(),
                1
            )
            if (address != null) {
                return address.getOrNull(0)!!.countryName
            }
        }
        return null
    } catch (e: Exception) {
        Log.d("TAG", "getCountryName: $e")
    }
    return null
}