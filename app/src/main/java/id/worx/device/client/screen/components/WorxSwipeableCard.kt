package id.worx.device.client.screen.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Surface
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeCardState {
    DEFAULT,
    LEFT,
    RIGHT
}

private const val MAXIMUM_DISTANCE = 90

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorxSwipeableCard(
    mainCard: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leftSwipeCard: (@Composable () -> Unit)? = null,
    rightSwipeCard: (@Composable () -> Unit)? = null,
    leftSwiped: (() -> Unit)? = null,
    rightSwiped: (() -> Unit)? = null,
    animationSpec: AnimationSpec<Float> = tween(250),
    thresholds: (from: SwipeCardState, to: SwipeCardState) -> ThresholdConfig = { _, _ ->
        FractionalThreshold(
            0.2f
        )
    },
    velocityThreshold: Dp = 125.dp
) {
    ConstraintLayout(modifier = modifier) {
        val (mainCardRef, actionCardRef) = createRefs()
        val swipeableState = rememberSwipeableState(
            initialValue = SwipeCardState.DEFAULT,
            animationSpec = animationSpec
        )
        val coroutineScope = rememberCoroutineScope()

        val swipeLeftCardVisible = remember { mutableStateOf(false) }

        val swipeEnabled = remember { mutableStateOf(true) }

        val maxWidthInPx = with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx()
        }

        val anchors = hashMapOf(0f to SwipeCardState.DEFAULT)
        if (leftSwipeCard != null)
            anchors[-maxWidthInPx] = SwipeCardState.LEFT

        if (rightSwipeCard != null)
            anchors[maxWidthInPx] = SwipeCardState.RIGHT

        Surface(
            color = Color.Transparent,
            content = if (swipeLeftCardVisible.value) {
                leftSwipeCard
            } else {
                rightSwipeCard
            } ?: {},
            modifier = Modifier
                .constrainAs(actionCardRef) {
                    top.linkTo(mainCardRef.top)
                    if (swipeLeftCardVisible.value) {
                        end.linkTo(mainCardRef.end)
                    } else {
                        start.linkTo(mainCardRef.start)
                    }
                    bottom.linkTo(mainCardRef.bottom)
                    height = Dimension.fillToConstraints
                }
        )

        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val tempOffset = swipeableState.offset.value.roundToInt()
                    val swipeToRight = tempOffset > 0
                    var offset =
                        if (swipeToRight) tempOffset.coerceAtMost(MAXIMUM_DISTANCE.dp.roundToPx())
                        else tempOffset.coerceAtLeast(-MAXIMUM_DISTANCE.dp.roundToPx())
                    if (offset < 0 && leftSwipeCard == null) offset = 0
                    if (offset > 0 && rightSwipeCard == null) offset = 0
                    IntOffset(offset, 0)
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Horizontal,
                    enabled = swipeEnabled.value,
                    thresholds = thresholds,
                    velocityThreshold = velocityThreshold
                )
                .constrainAs(mainCardRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }) {

            if (swipeableState.currentValue == SwipeCardState.LEFT && !swipeableState.isAnimationRunning) {
                leftSwiped?.invoke()
                coroutineScope.launch {
                    swipeEnabled.value = false
                    swipeableState.animateTo(SwipeCardState.DEFAULT)
                    swipeEnabled.value = true
                }
            } else if (swipeableState.currentValue == SwipeCardState.RIGHT && !swipeableState.isAnimationRunning) {
                rightSwiped?.invoke()
                coroutineScope.launch {
                    swipeEnabled.value = false
                    swipeableState.animateTo(SwipeCardState.DEFAULT)
                    swipeEnabled.value = true
                }
            }

            swipeLeftCardVisible.value = swipeableState.offset.value <= 0

            mainCard()
        }
    }
}