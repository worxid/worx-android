package id.worx.device.client.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.ui.graphics.toArgb
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import id.worx.device.client.theme.PrimaryMain
import java.io.IOException
import java.util.Locale
import kotlin.math.roundToInt

fun Activity.navigateToGallery(launcherGallery : ManagedActivityResultLauncher<Intent, ActivityResult>){
    FishBun.with(this)
        .setImageAdapter(CoilAdapter())
        .setMaxCount(1)
        .setThemeColor(PrimaryMain.toArgb())
        .startAlbumWithActivityResultCallback(launcherGallery)
}

fun Double.getTimeString(): String
{
    val resultInt = this.roundToInt()
    val hours = resultInt % 86400 / 3600
    val minutes = resultInt % 86400 % 3600 / 60
    val seconds = resultInt % 86400 % 3600 % 60

    return makeTimeString(hours, minutes, seconds)
}

private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02dh %02dm %02ds", hour, min, sec)

fun getReadableLocation(context: Context, latitude: Double?, longitude: Double?): String {
    var addressText = ""
    val geocoder = Geocoder(context, Locale.getDefault())

    Log.d("Helper - Lat", latitude.toString())
    Log.d("Helper - Lang", longitude.toString())

    try {
        var addresses: List<Address>? = mutableListOf()

        if (latitude != null && longitude != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                geocoder.getFromLocation(latitude, longitude, 1){
                    addresses = it
                }
            } else {
                addresses = geocoder.getFromLocation(latitude, longitude, 1)
            }
        }

        if (addresses?.isNotEmpty() == true){
            val address = addresses!![0]
            addressText = "${address.getAddressLine(0)}, ${address.locality}"
            Log.d("geolocation", addressText)
        }
    } catch (e: IOException){
        Log.d("geolocation", e.message.toString())
    }

    return addressText
}