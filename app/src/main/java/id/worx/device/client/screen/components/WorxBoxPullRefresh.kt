package id.worx.device.client.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorxBoxPullRefresh(
    modifier : Modifier = Modifier,
    onRefresh : ()  -> Unit,
    content : @Composable (BoxScope) -> Unit,
){
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshScope = rememberCoroutineScope()
    val threshold = with(LocalDensity.current) { 160.dp.toPx() }
    var currentDistance by remember { mutableStateOf(0f) }
    val progress = currentDistance / threshold

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        onRefresh()
        delay(1500)
        currentDistance = 0f
        isRefreshing = false
    }

    fun onPull(pullDelta: Float): Float = when {
        isRefreshing -> 0f
        else -> {
            val newOffset = (currentDistance + pullDelta).coerceAtLeast(0f)
            val dragConsumed = newOffset - currentDistance
            currentDistance = newOffset
            dragConsumed
        }
    }

    Box(
        modifier = modifier.pullRefresh(::onPull, { refresh() }),
    ){
        Box(content = content)
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = (isRefreshing || progress > 0)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(Modifier.wrapContentSize().padding(top = 24.dp))
            } else {
                CircularProgressIndicator(
                    progress,
                    Modifier
                        .wrapContentSize()
                        .padding(top = (50 * progress).dp)
                )
            }
        }
    }
}
