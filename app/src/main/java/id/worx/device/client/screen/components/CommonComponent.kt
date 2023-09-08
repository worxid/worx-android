package id.worx.device.client.screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import id.worx.device.client.screen.welcome.WelcomeEvent
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxCustomColorsPalette

enum class TransparentButtonType {
    NEGATIVE,
    NORMAL
}

@Composable
fun RedFullWidthButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = WorxCustomColorsPalette.current.button,
            contentColor = Color.White
        ),
        border = BorderStroke(
            1.5.dp,
            color = MaterialTheme.colors.onSecondary
        ),
        shape = RoundedCornerShape(1),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = onClickCallback
    ) {
        Text(text = label, style = Typography.button)
    }
}

@Composable
fun TransparentButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier,
    transparentButtonType: TransparentButtonType = TransparentButtonType.NORMAL
) {
    val contentColor =
        if (transparentButtonType == TransparentButtonType.NORMAL) WorxCustomColorsPalette.current.button else MaterialTheme.colors.onSecondary.copy(
            alpha = 0.6f
        )
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = contentColor
        ),
        border = BorderStroke(
            0.dp,
            color = Color.Transparent
        ),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = onClickCallback
    ) {
        Text(text = label, style = Typography.button, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ActionRedButton(
    modifier: Modifier,
    iconRes: Int,
    title: String,
    actionClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable { actionClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(WorxCustomColorsPalette.current.textFieldFocusedLabel)
                .padding(8.dp),
            painter = painterResource(id = iconRes),
            contentDescription = "Icon",
            tint = Color.White,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 1.dp),
            text = title,
            style = Typography.body2.copy(WorxCustomColorsPalette.current.textFieldFocusedLabel),
        )
    }
}

@Composable
fun WorxTopAppBar(
    onBack: () -> Unit,
    progress: Int? = null,
    title: String,
    useProgressBar: Boolean = true,
    isShowBackButton: Boolean = true,
    isShowMoreOptions: Boolean = false,
    onClickMoreOptions: () -> Unit = {}
) {
    Column {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = WorxCustomColorsPalette.current.appBar,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isShowBackButton) {
                    IconButton(
                        onClick = onBack,
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back Button",
                            tint = Color.White,
                        )
                    }
                }

                Text(
                    text = title,
                    style = Typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = true),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
                Box {
                    if (progress != null) {
                        CircularProgressIndicator(
                            progress = progress / 100.toFloat(),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterEnd)
                                .scale(0.75f),
                            color = Color.White,
                            strokeWidth = 3.dp,
                        )
                    }
                    if (useProgressBar) {
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterEnd)
                                .scale(0.75f),
                            color = Color.White.copy(0.3f),
                            strokeWidth = 3.dp,
                        )
                    }
                }

                if (isShowMoreOptions) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        modifier = Modifier.
                        clickable { onClickMoreOptions() },
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "option more"
                    )
                }
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = WorxCustomColorsPalette.current.appBarDivider,
        )
    }
}

@Composable
fun WhiteFullWidthButton(
    modifier: Modifier,
    label: String,
    onClickEvent: () -> Unit = {},
    onClickCallback: (WelcomeEvent) -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary.copy(0.2f),
            contentColor = MaterialTheme.colors.primary
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
        shape = RoundedCornerShape(1),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = {
            onClickCallback(WelcomeEvent.JoinTeam)
            onClickEvent()
        }) {
        Text(text = label, style = Typography.button)
    }
}

@Composable
fun WorxThemeStatusBar() {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colors.primaryVariant

    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }
}