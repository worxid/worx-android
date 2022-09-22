package id.worx.device.client

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Value
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Util {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }

    fun getCurrentDate(dateFormat: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateTime = LocalDateTime.now()
            dateTime.format(DateTimeFormatter.ofPattern(dateFormat))
        } else {
            val calendar = Calendar.getInstance()
            SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
        }
    }

    /**
     * If the form is half filled, set the progress value
     */
    fun initProgress(values: MutableMap<String, Value>, fields: ArrayList<Fields>): Int{
        val progressBit = 100 / fields.size
        if (values.isNotEmpty()) {
            return progressBit * values.size
        }
        return 0
    }
}