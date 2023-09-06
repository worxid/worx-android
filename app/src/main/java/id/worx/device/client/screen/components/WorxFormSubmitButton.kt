package id.worx.device.client.screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.RedDarkButton
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

/**
 * Created By : Jonathan Darwin on August 31, 2023
 */
@Composable
fun WorxFormSubmitButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier,
    theme: String?
) {
    ConstraintLayout(
        modifier = modifier.clickable {
            onClickCallback()
        }
    ) {
        val (
            outlinedButton,
            shadowButton,
        ) = createRefs()

        Box(
            modifier = Modifier
                .background(Color.Black)
                .constrainAs(shadowButton) {
                    top.linkTo(parent.top, 2.dp)
                    start.linkTo(parent.start, 2.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )

        OutlinedButton(
            modifier = Modifier
                .constrainAs(outlinedButton) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end, 2.dp)
                    bottom.linkTo(parent.bottom, 2.dp)
                },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (theme == SettingTheme.Dark || theme == SettingTheme.System) RedDarkButton else MaterialTheme.colors.primary,
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
            ConstraintLayout(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                val (
                    logo,
                    text,
                ) = createRefs()

                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Send Icon",
                    modifier = Modifier.constrainAs(logo) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    style = Typography.button,
                    modifier = Modifier
                        .animateContentSize()
                        .constrainAs(text) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(logo.end, if (label.isEmpty()) 0.dp else 8.dp)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun WorxFormSubmitButtonPreview() {
    WorxTheme {
        WorxFormSubmitButton(
            onClickCallback = {  },
            label = "Submit",
            modifier = Modifier,
            theme = null
        )
    }
}