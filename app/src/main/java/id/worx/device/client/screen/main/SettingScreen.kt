package id.worx.device.client.screen.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.BuildConfig
import id.worx.device.client.R
import id.worx.device.client.Util
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.WhiteFullWidthButton
import id.worx.device.client.screen.components.WorxDialog
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.screen.components.getActivity
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.HomeVMPrev
import id.worx.device.client.viewmodel.HomeViewModel
import id.worx.device.client.viewmodel.ThemeVMMock
import id.worx.device.client.viewmodel.ThemeViewModel

object SettingTheme {
    val System = "System default"
    val Dark = "Dark"
    val Green = "Green"
    val Blue = "Blue"
}

@SuppressLint("HardwareIds")
@Composable
fun SettingScreen(
    viewModel: HomeViewModel,
    session: Session,
    themeViewModel: ThemeViewModel,
    onBackNavigation: () -> Unit
) {
    val theme = session.theme
    val showDialogLeave = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    if (showDialogLeave.value) {
        WorxDialog(
            content = {
                LeaveOrganizationDialog(setShowDialog = {
                    showDialogLeave.value = it
                },
                    onPositiveButton = {
                        viewModel.leaveTeam(
                            onSuccess = {
                                Toast.makeText(context, "SUCCESS LEAVE ORG", Toast.LENGTH_SHORT)
                                    .show()
                                context.getActivity()?.finishAffinity()
                            },
                            onError = {
                                Toast.makeText(context, "Something Error", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            deviceCode = Util.getDeviceCode(context)
                        )
                    })
            },
            setShowDialog = { showDialogLeave.value = it })
    }

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = stringResource(id = R.string.settings),
                useProgressBar = false
            )
        }
    ) { paddingValues ->
        val verticalScroll = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(verticalScroll)
                .background(MaterialTheme.colors.secondary)
        ) {
            HeaderTileSetting(title = stringResource(id = R.string.organization_details))
            TileItemSetting(
                title = stringResource(id = R.string.organizations),
                subtitle = session.organization,
                session = session
            )
            TileItemSetting(
                title = stringResource(id = R.string.organizations_key),
                subtitle = session.organizationKey,
                session = session
            )
            TileItemSetting(
                title = stringResource(id = R.string.device_name),
                subtitle = session.deviceName,
                session = session
            )
            Divider(color = GrayDivider, modifier = Modifier.padding(top = 20.dp))
            HeaderTileSetting(title = stringResource(id = R.string.devices_settings))
            TileItemTheme( themeViewModel = themeViewModel, session = session)
            TileItemSetting(
                title = stringResource(id = R.string.save_image_in_gallery),
                subtitle = stringResource(id = R.string.save_image_in_gallery_sub),
                iconRes = R.drawable.ic_baseline_collections_24,
                toggleActive = true,
                session = session
            )
            TileItemSetting(
                title = stringResource(id = R.string.advance_settings),
                subtitle = stringResource(id = R.string.switch_to_server),
                iconRes = R.drawable.ic_settings,
                onPress = { viewModel.goToAdvanceSettings() },
                toggleActive = false,
                session = session
            )
            HeaderTileSetting(title = stringResource(id = R.string.about_this_app))
            TileItemSetting(
                title = stringResource(id = R.string.app_version),
                subtitle = BuildConfig.VERSION_NAME,
                session = session
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_version_code),
                subtitle = BuildConfig.VERSION_CODE.toString(),
                session = session
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_package_name),
                subtitle = BuildConfig.APPLICATION_ID,
                session = session
            )
            HeaderTileSetting(title = stringResource(id = R.string.legal))
            TileItemSetting(
                title = stringResource(id = R.string.open_source_licenses), onPress = {
                    viewModel.goToLicencesScreen()
                },
                session = session
            )
            WhiteFullWidthButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Leave Organization",
                onClickEvent = {
                    showDialogLeave.value = !showDialogLeave.value
                },
                theme = theme,
                onClickCallback = {})
        }
    }
}

@Preview
@Composable
fun TestDialog() {
    WorxTheme() {
        WorxDialog(content = { LeaveOrganizationDialog(setShowDialog = {}) }, setShowDialog = {})
    }
}

@Composable
fun LeaveOrganizationDialog(
    setShowDialog: (Boolean) -> Unit = {},
    onPositiveButton: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.secondary)
    ) {
        val (tvTitle, tvYes, tvCancel) = createRefs()

        Text(
            text = stringResource(id = R.string.leave_organization),
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = stringResource(id = R.string.yes),
            style = Typography.body2.copy(MaterialTheme.colors.onBackground),
            modifier = Modifier
                .constrainAs(tvYes) {
                    top.linkTo(tvTitle.bottom, 32.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                    onPositiveButton()
                }
        )
        Text(
            text = stringResource(id = R.string.cancel),
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .constrainAs(tvCancel) {
                    top.linkTo(tvTitle.bottom, 32.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(tvYes.start, 38.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                }
        )
    }
}


@Composable
fun TileItemTheme(
    themeViewModel: ThemeViewModel,
    session: Session
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        val (icon, tvTitle, red, dark, green, blue) = createRefs()
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_color_lens_24),
            contentDescription = "Icon",
            modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            },
            tint = MaterialTheme.colors.onSecondary
        )
        Text(
            text = stringResource(id = R.string.theme),
            modifier = Modifier
                .constrainAs(tvTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(
                        icon.end,
                        18.dp
                    )
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            style = Typography.body2,
            color = MaterialTheme.colors.onSecondary,
        )
        BoxTheme(
            Modifier.constrainAs(red) {
            top.linkTo(tvTitle.bottom, 7.dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(tvTitle.start)
        }, SettingTheme.System, themeViewModel, session, PrimaryMain)

        BoxTheme(Modifier.constrainAs(dark) {
            top.linkTo(red.top)
            bottom.linkTo(red.bottom)
            start.linkTo(red.end, 20.dp)
        } , SettingTheme.Dark, themeViewModel , session, PrimaryMainDark )

        BoxTheme(Modifier.constrainAs(blue) {
            top.linkTo(dark.top)
            bottom.linkTo(dark.bottom)
            start.linkTo(dark.end, 20.dp)
        }, SettingTheme.Blue , themeViewModel, session, PrimaryMainBlue )

        BoxTheme(Modifier.constrainAs(green) {
            top.linkTo(blue.top)
            bottom.linkTo(blue.bottom)
            start.linkTo(blue.end, 20.dp)
        }, SettingTheme.Green, themeViewModel , session, PrimaryMainGreen )
    }
}

@Composable
private fun BoxTheme(
    modifier: Modifier,
    selectedTheme: String,
    themeViewModel: ThemeViewModel,
    session: Session,
    themeColor: Color
) {
    Box(
        modifier = modifier
            .clickable {
                themeViewModel.onThemeChanged(selectedTheme)
                session.setTheme(selectedTheme)
            }
            .clip(RoundedCornerShape(2.dp))
            .background(
                if (session.theme?.equals(selectedTheme) == true) MaterialTheme.colors.primary.copy(
                    0.2f
                ) else Color.White.copy(0f)
            )
            .size(36.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(themeColor)
                .size(32.dp)
        )
    }
}


@Composable
fun TileItemSetting(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    iconRes: Int? = null,
    toggleActive: Boolean = false,
    onPress: () -> Unit = {},
    session: Session?
) {
    val toggleValue = session?.isSaveImageToGallery ?: false
    val checkStateSwitch = remember { mutableStateOf(toggleValue) }
    val theme = session?.theme
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPress() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        val (icon, tvTitle, tvSubtitle, button) = createRefs()
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Icon",
                modifier = modifier.constrainAs(icon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
                tint = MaterialTheme.colors.onSecondary
            )
        }
        Text(
            text = title,
            modifier = modifier
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
            color = MaterialTheme.colors.onSecondary,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                modifier = modifier.constrainAs(tvSubtitle) {
                    top.linkTo(tvTitle.bottom, 5.dp)
                    start.linkTo(tvTitle.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(tvTitle.end)
                    width = Dimension.fillToConstraints
                },
                style = Typography.body1,
                color = MaterialTheme.colors.onSecondary.copy(0.54f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (toggleActive) {
            Switch(
                checked = checkStateSwitch.value,
                onCheckedChange = {
                    checkStateSwitch.value = it
                    session?.isSaveImageToGallery(it)
                },
                modifier = modifier.constrainAs(button) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    uncheckedThumbColor = MaterialTheme.colors.surface,
                    checkedTrackColor = MaterialTheme.colors.primary,
                    uncheckedTrackColor = MaterialTheme.colors.surface,
                )
            )
        }
    }
}

@Composable
fun HeaderTileSetting(
    title: String,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp, vertical = 20.dp)
    ) {
        Text(text = title, style = Typography.body1, color = MaterialTheme.colors.onSecondary)
    }
}

@Preview (showSystemUi = true)
@Composable
fun PreviewSettingScreen(){
    WorxTheme() {
        SettingScreen(
            viewModel = HomeVMPrev(),
            session = Session(LocalContext.current),
            themeViewModel =ThemeVMMock() ) {
        }
    }
}
