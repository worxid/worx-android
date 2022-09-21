package id.worx.device.client

import android.content.Context
import android.net.ConnectivityManager

object Util {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }
}