package id.worx.device.client.screen.main

import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.BuildConfig
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.WhiteFullWidthButton
import id.worx.device.client.screen.WorxTopAppBar
import id.worx.device.client.screen.components.WorxDialog
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.HomeViewModel

@Composable
fun SettingScreen(
    viewModel: HomeViewModel,
    session: Session,
    onBackNavigation: () -> Unit
) {
    val showDialogTheme = remember {
        mutableStateOf(false)
    }
    val showDialogLeave = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    if (showDialogTheme.value) {
        WorxDialog(
            content = {
                ThemeDialog(
                    setShowDialog = { showDialogTheme.value = it },
                    session = session
                )
            },
            setShowDialog = { showDialogTheme.value = it })
    }

    if (showDialogLeave.value) {
        WorxDialog(
            content = { LeaveOrganizationDialog(setShowDialog = { showDialogLeave.value = it }) },
            setShowDialog = { showDialogLeave.value = it })
    }

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
                subtitle = Settings.Secure.getString(context.contentResolver, "bluetooth_name"),
            )
            Divider(color = GrayDivider, modifier = Modifier.padding(top = 20.dp))
            HeaderTileSetting(title = stringResource(id = R.string.devices_settings))
            TileItemSetting(
                title = stringResource(id = R.string.theme),
                subtitle = session.theme,
                iconRes = R.drawable.ic_baseline_color_lens_24,
                modifier = Modifier.clickable {
                    showDialogTheme.value = !showDialogTheme.value
                }
            )
            TileItemSetting(
                title = stringResource(id = R.string.save_image_in_gallery),
                subtitle = stringResource(id = R.string.save_image_in_gallery_sub),
                iconRes = R.drawable.ic_baseline_collections_24,
                toggleActive = true,
                session = session
            )
            HeaderTileSetting(title = stringResource(id = R.string.about_this_app))
            TileItemSetting(
                title = stringResource(id = R.string.app_version),
                subtitle = BuildConfig.VERSION_NAME,
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_version_code),
                subtitle = BuildConfig.VERSION_CODE.toString(),
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_package_name),
                subtitle = BuildConfig.APPLICATION_ID,
            )
            HeaderTileSetting(title = stringResource(id = R.string.legal))
            TileItemSetting(title = stringResource(id = R.string.open_source_licenses), onPress = {
                viewModel.goToLicencesScreen()
            })
            WhiteFullWidthButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Leave Organization",
                onClickEvent = {
                    showDialogLeave.value = !showDialogLeave.value
                },
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
    setShowDialog: (Boolean) -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (tvTitle, tvYes, tvCancel) = createRefs()

        Text(
            text = stringResource(id = R.string.leave_organization),
            style = Typography.body2,
            color = BlackFont,
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = stringResource(id = R.string.yes),
            style = Typography.body2,
            color = RedDark,
            modifier = Modifier
                .constrainAs(tvYes) {
                    top.linkTo(tvTitle.bottom, 32.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                }
        )
        Text(
            text = stringResource(id = R.string.cancel),
            style = Typography.body2,
            color = BlackVariantFont,
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
fun ThemeDialog(
    setShowDialog: (Boolean) -> Unit,
    session: Session
) {
    val rbOptions = arrayListOf("System", "Dark", "Green", "Blue")
    val selectedTheme = session.theme
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(selectedTheme)
    }
    Column(modifier = Modifier.selectableGroup()) {
        Text(
            text = stringResource(id = R.string.theme),
            style = Typography.body2,
            color = BlackFont,
            fontWeight = FontWeight.W500
        )
        rbOptions.forEach { s ->
            ConstraintLayout(
                modifier = Modifier
                    .selectableGroup()
                    .selectable(
                        selected = (s == selectedOption),
                        onClick = {
                            onOptionSelected(s)
                            session.setTheme(s)
                            setShowDialog(false)
                        },
                        role = Role.RadioButton
                    )
            ) {
                val (tvItem, rbItem) = createRefs()

                RadioButton(
                    selected = (s == selectedOption),
                    onClick = {
                        onOptionSelected(s)
                        session.setTheme(s)
                        setShowDialog(false)
                    },
                    modifier = Modifier.constrainAs(rbItem) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                    colors = RadioButtonDefaults.colors(PrimaryMain)
                )
                Text(
                    text = s,
                    style = Typography.body1,
                    color = BlackFont,
                    modifier = Modifier.constrainAs(tvItem) {
                        top.linkTo(rbItem.top)
                        bottom.linkTo(rbItem.bottom)
                        start.linkTo(rbItem.end, 8.dp)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TileItemSetting(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    iconRes: Int? = null,
    toggleActive: Boolean = false,
    onPress: () -> Unit = {},
    session: Session? = null
) {
    val toggleValue = session?.isSaveImageToGallery ?: false
    val checkStateSwitch = remember { mutableStateOf(toggleValue) }

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
                }
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
            color = BlackFont,
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
                color = BlackVariantFont,
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
                    checkedThumbColor = RedDarkButton,
                    uncheckedThumbColor = RedDarkButton,
                    checkedTrackColor = RedDarkButton,
                    uncheckedTrackColor = RedDarkButton,
                )
            )
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
