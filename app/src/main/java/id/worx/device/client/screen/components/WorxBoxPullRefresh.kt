package id.worx.device.client.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        onRefresh()
        delay(1500)
        isRefreshing = false
    }

    val state = rememberPullRefreshState(isRefreshing, ::refresh)

    Box(
        modifier = modifier.pullRefresh(state)
    ){
        Box(content = content)
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = (isRefreshing )
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(Modifier.wrapContentSize().padding(top = 68.dp))
            }
        }
    }
}
