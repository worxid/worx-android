package id.worx.device.client.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import id.worx.device.client.R
import id.worx.device.client.screen.WorxTopAppBar
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography

@Composable
fun OpenSourceLicensesScreen(
    onBackNavigation: () -> Unit
) {
    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = stringResource(id = R.string.open_source_licenses_cap)
            )
        }
    ) { paddingValues ->
        val verticalScroll = rememberScrollState()
        val context = LocalContext.current

        val libs = Libs.Builder()
            .withContext(context)
            .build()
        val libraries = libs.libraries // retrieve all libraries defined in the metadata
        val licenses = libs.licenses

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(libraries) { lib ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(text = lib.name, style = Typography.body1.copy(Color.Black))
                    Divider(color = GrayDivider, modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }
}