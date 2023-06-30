package id.worx.device.client.screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.theme.WorxTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color.White,
    borderStroke: BorderStroke = BorderStroke(1.dp, Color.Black),
    elevation: Dp = 8.dp,
    icon: @Composable () -> Unit,
    text: String,
    swipeDirection: SwipeDirection = SwipeDirection.LeftToRight,
    onSwipe: () -> Unit
) {
    val initialValue = when(swipeDirection){
        SwipeDirection.LeftToRight -> 0
        SwipeDirection.RightToLeft -> 1
    }
    val swipeableState = rememberSwipeableState(initialValue = initialValue)

    val textAlpha by animateFloatAsState(
        if (swipeableState.offset.value > 10f) (1 - swipeableState.progress.fraction) else 1f
    )

    if (swipeableState.isAnimationRunning) {
        DisposableEffect(key1 = Unit) {
            onDispose {
                val value = when(swipeDirection){
                    SwipeDirection.LeftToRight -> 1
                    SwipeDirection.RightToLeft -> 0
                }
                if (swipeableState.currentValue == value) onSwipe()
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = backgroundColor,
        border = borderStroke,
        elevation = elevation
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            var iconSize by remember {
                mutableStateOf(IntSize.Zero)
            }
            val maxWidth = with(LocalDensity.current) {
                when(swipeDirection){
                    SwipeDirection.LeftToRight -> this@BoxWithConstraints.maxWidth.toPx() - iconSize.width
                    SwipeDirection.RightToLeft -> -this@BoxWithConstraints.maxWidth.toPx() + iconSize.width
                }
            }

            val anchors = when(swipeDirection){
                SwipeDirection.LeftToRight -> mapOf(0f to 0, maxWidth to 1)
                SwipeDirection.RightToLeft -> mapOf(0f to 0, -maxWidth to 1)
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                text = text.uppercase(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.body2,
            )
            Box(modifier = Modifier
                .onGloballyPositioned {
                    iconSize = it.size
                }
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                )
            ) {
                Surface(
                    modifier = Modifier,
                    shape = CircleShape,
                    color = Color.White,
                    elevation = elevation,
                    border = borderStroke
                ) {
                    icon()
                }
            }
        }
    }
}

@Preview
@Composable
fun SwipeButtonPreview() {
    WorxTheme {
        SwipeButton(icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }, text = "Swipe") {

        }
    }
}

enum class SwipeDirection {
    LeftToRight, RightToLeft
}