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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.BuildConfig
import id.worx.device.client.R
import id.worx.device.client.Util
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.TransparentButton
import id.worx.device.client.screen.components.TransparentButtonType
import id.worx.device.client.screen.components.WorxDialog
import id.worx.device.client.screen.components.WorxThemeBottomSheet
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

enum class AppTheme(val value: String) {
    LIGHT("Light"),
    DARK("Dark"),
    DEVICE_SYSTEM("Device System")
}

fun String.getAppTheme(): AppTheme {
    return when (this) {
        "Device System" -> AppTheme.DEVICE_SYSTEM
        else -> AppTheme.valueOf(this.uppercase())
    }
}



@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("HardwareIds")
@Composable
fun SettingScreen(
    viewModel: HomeViewModel,
    session: Session,
    themeViewModel: ThemeViewModel
) {
    val selectedTheme = session.theme.getAppTheme()
    val showDialogLeave = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

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

    val verticalScroll = rememberScrollState()
    val colorPalette = WorxCustomColorsPalette.current

    WorxThemeBottomSheet(sheetState = sheetState, selectedTheme = selectedTheme, onThemeClicked = {
        themeViewModel.onThemeChanged(it.value)
        session.setTheme(it.value)
    }) { openThemeBottomSheet ->
        Column(
            modifier = Modifier
                .verticalScroll(verticalScroll)
                .background(colorPalette.homeBackground)
                .padding(16.dp)
        ) {
            HeaderTileSetting(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                title = stringResource(id = R.string.organization_details)
            )
            TileItemSetting(
                title = stringResource(id = R.string.organizations),
                subtitle = session.organization,
                iconRes = R.drawable.ic_organization,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            )
            SettingDivider()
            TileItemSetting(
                title = stringResource(id = R.string.organizations_key),
                subtitle = session.organizationKey,
                iconRes = R.drawable.ic_organization_key,
                session = session
            )
            SettingDivider()
            TileItemSetting(
                title = stringResource(id = R.string.device_name),
                subtitle = session.deviceName,
                iconRes = R.drawable.ic_device_name,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            )
            HeaderTileSetting(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                title = stringResource(id = R.string.devices_settings)
            )
//        TileItemSetting(
//            title = stringResource(id = R.string.advance_settings),
//            subtitle = stringResource(id = R.string.switch_to_server),
//            iconRes = R.drawable.ic_settings,
//            onPress = { viewModel.goToAdvanceSettings() },
//            toggleActive = false,
//            session = session
//        )
            TileItemSetting(
                title = stringResource(id = R.string.theme),
//                subtitle = session.theme,
                subtitle = session.theme,
                iconRes = R.drawable.ic_color_theme,
                showChevron = true,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                onPress = { openThemeBottomSheet() }
            )
            TileItemSetting(
                title = stringResource(id = R.string.save_image_in_gallery),
                subtitle = stringResource(id = R.string.save_image_in_gallery_sub),
                iconRes = R.drawable.ic_collections,
                toggleActive = true,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            )
            HeaderTileSetting(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                title = stringResource(id = R.string.about_this_app)
            )
            TileItemSetting(
                title = stringResource(id = R.string.app_version),
                subtitle = BuildConfig.VERSION_NAME,
                iconRes = R.drawable.ic_app_version,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            )
            SettingDivider()
            TileItemSetting(
                title = stringResource(id = R.string.app_version_code),
                subtitle = BuildConfig.VERSION_CODE.toString(),
                iconRes = R.drawable.ic_app_version_code,
                session = session
            )
            SettingDivider()
            TileItemSetting(
                title = stringResource(id = R.string.app_package_name),
                subtitle = BuildConfig.APPLICATION_ID,
                iconRes = R.drawable.ic_package_name,
                session = session,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            )
            HeaderTileSetting(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                title = stringResource(id = R.string.legal),
            )
            TileItemSetting(
                title = stringResource(id = R.string.open_source_licenses), onPress = {
                    viewModel.goToLicencesScreen()
                },
                iconRes = R.drawable.ic_organization,
                session = session,
                subtitle = stringResource(id = R.string.open_source),
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Leave Organization",
                onClickCallback = {
                    showDialogLeave.value = !showDialogLeave.value
                }
            )
        }
    }
}

@Composable
fun SettingDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.12f)
    )
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
        val (tvTitle, tvDesc, tvYes, tvCancel) = createRefs()

        Text(
            text = stringResource(id = R.string.leave_organization_title),
            style = Typography.h6,
            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
            lineHeight = 27.sp,
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(id = R.string.leave_organization),
            style = Typography.body2,
            lineHeight = 24.sp,
            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
            modifier = Modifier.constrainAs(tvDesc) {
                top.linkTo(tvTitle.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        TransparentButton(
            label = stringResource(id = R.string.confirm),
            modifier = Modifier
                .constrainAs(tvYes) {
                    top.linkTo(tvDesc.bottom, 8.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            onClickCallback = {
                setShowDialog(false)
                onPositiveButton()
            }
        )

        TransparentButton(
            label = stringResource(id = R.string.cancel),
            modifier = Modifier
                .constrainAs(tvCancel) {
                    top.linkTo(tvDesc.bottom, 8.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(tvYes.start, 8.dp)
                    width = Dimension.fillToConstraints
                },
            onClickCallback = {
                setShowDialog(false)
            },
            transparentButtonType = TransparentButtonType.NEGATIVE
        )
    }
}

@Composable
fun TileItemSetting(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    iconRes: Int,
    toggleActive: Boolean = false,
    onPress: () -> Unit = {},
    session: Session?,
    showChevron: Boolean = false
) {
    val toggleValue = session?.isSaveImageToGallery ?: false
    val checkStateSwitch = remember { mutableStateOf(toggleValue) }
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPress() }
            .background(WorxCustomColorsPalette.current.bottomSheetBackground)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        val (icon, tvTitle, tvSubtitle, button) = createRefs()
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Icon",
            modifier = modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            },
            tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
        )
        Text(
            text = title,
            modifier = modifier
                .constrainAs(tvTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(
                        icon.end,
                        12.dp
                    )
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
            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
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
                style = Typography.caption,
                color = MaterialTheme.colors.onSecondary.copy(0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Chevron Right",
                tint = WorxCustomColorsPalette.current.button,
                modifier = modifier.constrainAs(button) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
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
    modifier: Modifier = Modifier,
    title: String,
) {
    ConstraintLayout(modifier = modifier) {
        Text(
            text = title,
            style = Typography.caption,
            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.38f)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSettingScreen() {
    WorxTheme {
        SettingScreen(
            viewModel = HomeVMPrev(),
            session = Session(LocalContext.current),
            themeViewModel = ThemeVMMock()
        )
    }
}
