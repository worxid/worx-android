package id.worx.device.client.util

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import id.worx.device.client.theme.PrimaryMain

fun Activity.navigateToGallery(launcherGallery : ManagedActivityResultLauncher<Intent, ActivityResult>){
    FishBun.with(this)
        .setImageAdapter(CoilAdapter())
        .setMaxCount(1)
        .setThemeColor(PrimaryMain.toArgb())
        .startAlbumWithActivityResultCallback(launcherGallery)
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }