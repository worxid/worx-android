package id.worx.device.client

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        if (Util.isConnected(this)){
            syncWithServer()
        }
    }

    private fun syncWithServer() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.uploadSubmissionWork()
            viewModel.downloadFormTemplate(this@MainActivity)
            {
                Toast.makeText(this@MainActivity, "Sync DB with server is done", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}