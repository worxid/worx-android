package id.worx.device.client.screen.main

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import id.worx.device.client.R
import id.worx.device.client.screen.WorxTopAppBar
import id.worx.device.client.theme.BlackFont
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.util.excludeLibraries

@Composable
fun LicencesScreen(
    onBackNavigation: () -> Unit,
    librariesBlock: (Context) -> Libs = {
        Libs.Builder().withContext(it).build()
    }
) {
    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = stringResource(id = R.string.open_source_licences_cap)
            )
        }
    ) { padding ->
        val context = LocalContext.current
        val libraries = remember { mutableStateOf<Libs?>(null) }
        LaunchedEffect(libraries) {
            libraries.value = librariesBlock.invoke(context)
        }

        val libs = arrayListOf<Library>()
        libraries.value?.libraries?.forEach { lib ->
            libs.add(lib)
        }

        var setOfLibs = libs.toMutableList()
        setOfLibs.replaceAll {
            Library(
                it.uniqueId,
                it.artifactVersion,
                it.name,
                it.description,
                it.website,
                it.developers,
                it.organization,
                it.scm,
                it.licenses,
                it.funding,
                it.tag
            )
        }

        //exclude some libraries
        excludeLibraries.forEach { exclude ->
            setOfLibs = setOfLibs.filter {
                !it.uniqueId.contains(exclude)
            }.toMutableList()
        }

        val apanih = setOfLibs.groupBy({it.uniqueId},{it.name})
        Log.d(TAG, "LicencesScreen: data $apanih")

        setOfLibs.forEach {
            Log.d(TAG, "LicencesScreen:  $it")
        }

        //get
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            items(setOfLibs.size ?: 0) { index ->
                Text(
//                    text = "${setOfLibs[index].uniqueId} - ${setOfLibs[index].name}",
                    text = setOfLibs[index].name,
                    style = Typography.body2,
                    color = BlackFont,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ItemLazyColumn(lib: Library) {
    ConstraintLayout() {
        Text(text = lib.artifactId)
    }
}

@Preview
@Composable
fun previewLicencesScreen() {
    WorxTheme() {
        LicencesScreen(onBackNavigation = { /*TODO*/ })
    }
}