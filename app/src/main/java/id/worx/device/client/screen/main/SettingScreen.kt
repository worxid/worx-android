package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.R
import id.worx.device.client.screen.WhiteFullWidthButton
import id.worx.device.client.screen.WorxTopAppBar
import id.worx.device.client.theme.*

@Composable
fun SettingScreen(
    onBackNavigation: () -> Unit
) {
    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = stringResource(id = R.string.settings)
            )
        }
    ) { paddingValues ->
        val verticalScroll = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(verticalScroll)
        ) {
            HeaderTileSetting(title = stringResource(id = R.string.organization_details))
            TileItemSetting(
                title = stringResource(id = R.string.organizations),
                subtitle = "Fields Service Mobile",
            )
            TileItemSetting(
                title = stringResource(id = R.string.organizations_key),
                subtitle = "AIT763",
            )
            TileItemSetting(
                title = stringResource(id = R.string.device_name),
                subtitle = "Budiman",
            )
            Divider(color = GrayDivider, modifier = Modifier.padding(top = 20.dp))
            HeaderTileSetting(title = stringResource(id = R.string.devices_settings))
            TileItemSetting(
                title = stringResource(id = R.string.theme),
                subtitle = "System default",
                iconRes = R.drawable.ic_baseline_color_lens_24
            )
            TileItemSetting(
                title = stringResource(id = R.string.save_image_in_gallery),
                subtitle = stringResource(id = R.string.save_image_in_gallery_sub),
                iconRes = R.drawable.ic_baseline_collections_24,
                toggleActive = true,
                onPressToggle = {}
            )
            HeaderTileSetting(title = stringResource(id = R.string.about_this_app))
            TileItemSetting(
                title = stringResource(id = R.string.app_version),
                subtitle = "1.8.0",
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_version_code),
                subtitle = "1808",
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_package_name),
                subtitle = "worx.id",
            )
            HeaderTileSetting(title = stringResource(id = R.string.legal))
            TileItemSetting(title = stringResource(id = R.string.open_source_licenses))
            WhiteFullWidthButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Leave Organization",
                onClickCallback = {})
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TileItemSetting(
    title: String,
    subtitle: String? = null,
    iconRes: Int? = null,
    toggleActive: Boolean = false,
    onPressToggle: () -> Unit? = {}
) {
    val checkStateSwitch = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        val (icon, tvTitle, tvSubtitle, button) = createRefs()
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Icon",
                modifier = Modifier.constrainAs(icon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .constrainAs(tvTitle) {
                    top.linkTo(parent.top)
                    if (iconRes == null) {
                        start.linkTo(parent.start, 40.dp)
                    } else {
                        start.linkTo(
                            icon.end,
                            18.dp
                        )
                    }
                    if (toggleActive) {
                        end.linkTo(button.start)
                    } else {
                        end.linkTo(parent.end)
                    }
                    if (subtitle == null) {
                        bottom.linkTo(parent.bottom)
                    }
                    width = Dimension.fillToConstraints
                },
            style = Typography.body2,
            color = BlackFont,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                modifier = Modifier.constrainAs(tvSubtitle) {
                    top.linkTo(tvTitle.bottom, 5.dp)
                    start.linkTo(tvTitle.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(tvTitle.end)
                    width = Dimension.fillToConstraints
                },
                style = Typography.body1,
                color = BlackVariantFont,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (toggleActive) {
            Switch(checked = checkStateSwitch.value, onCheckedChange = {
                onPressToggle
                checkStateSwitch.value = it
            }, modifier = Modifier.constrainAs(button) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }, colors = SwitchDefaults.colors(
                checkedThumbColor = RedDarkButton,
                uncheckedThumbColor = RedDarkButton,
                checkedTrackColor = RedDarkButton,
                uncheckedTrackColor = RedDarkButton,
            ))
        }
    }
}

@Composable
fun HeaderTileSetting(
    title: String
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp, vertical = 20.dp)
    ) {
        Text(text = title, style = Typography.body1, color = BlackVariantFont)
    }
}

@Preview
@Composable
fun PreviewSettingScreen() {
    WorxTheme {
        SettingScreen({})
    }
}