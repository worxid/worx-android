package id.worx.mobile.util

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import id.worx.mobile.screen.main.isLightMode
import id.worx.mobile.theme.LocalAppTheme
import id.worx.mobile.theme.PrimaryMain

fun Activity.navigateToGallery(launcherGallery: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    FishBun.with(this)
        .setImageAdapter(CoilAdapter())
        .setMaxCount(1)
        .setThemeColor(PrimaryMain.toArgb())
        .startAlbumWithActivityResultCallback(launcherGallery)
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun Int.getDrawableBasedOnTheme(darkModeResource: Int): Int {
    return if (LocalAppTheme.current.isLightMode()) {
        this
    } else {
        darkModeResource
    }
}