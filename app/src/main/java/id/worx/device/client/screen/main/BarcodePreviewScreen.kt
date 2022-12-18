package id.worx.device.client.screen.main

import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import id.worx.device.client.R
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto

@Composable
fun BarcodePreviewScreen(
    filePath: String?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.onError,
                contentColor = MaterialTheme.colors.onError
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    val (tvCancel, tvPreview) = createRefs()
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = Typography.subtitle1.copy(
                            color = MaterialTheme.colors.onBackground,
                            fontFamily = fontRoboto
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(tvCancel) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.preview),
                        style = Typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = fontRoboto
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(tvPreview) {
                            top.linkTo(tvCancel.top)
                            bottom.linkTo(tvCancel.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { _ ->
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.onError)
        ) {
            val (image, container) = createRefs()
            AsyncImage(
                model = if (filePath?.contains("File") == true) {
                    android.R.drawable.ic_menu_gallery
                } else {
                    filePath
                },
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.54f))
                    .height(80.dp)
                    .padding(horizontal = 24.dp)
                    .constrainAs(container) {
                        bottom.linkTo(parent.bottom)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    tint = Color.White,
                    contentDescription = "Collection Image"
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_confirm),
                        contentDescription = "Confirm photo",
                        tint = Color.White,
                    )
                    Text(
                        text = stringResource(id = R.string.done).uppercase(),
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        fontFamily = fontRoboto
                    )
                }
            }
        }
    }
}