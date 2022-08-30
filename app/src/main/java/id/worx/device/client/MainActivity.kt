package id.worx.device.client

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import id.worx.device.client.screen.HomeScreen
import id.worx.device.client.theme.WorxTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorxTheme {
                HomeScreen()
            }
        }

    }
}