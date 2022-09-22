package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import id.worx.device.client.R
import id.worx.device.client.screen.WorxTopAppBar
import id.worx.device.client.theme.WorxTheme

@Composable
fun LicencesScreen(
    onBackNavigation: () -> Unit
) {
    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = stringResource(id = R.string.open_source_licences)
            )
        }
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            showAuthor = false,
            showLicenseBadges = false,
            showVersion = false,
            onLibraryClick = {}
        )
    }
}

@Preview
@Composable
fun previewLicencesScreen() {
    WorxTheme() {
        LicencesScreen {

        }
    }
}