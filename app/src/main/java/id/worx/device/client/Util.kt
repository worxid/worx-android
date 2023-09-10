package id.worx.device.client

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import id.worx.device.client.data.upload.CustomPlaceholdersProcessor
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


object Util {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        return isConnectedToInternet(connectivityManager)
    }

    fun isConnectedToInternet(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)
        if (connection != null) {
            val hasInternetCapabilities = connection.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasValidatedCapabilities = connection.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val hasCellularConnection = connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val hasWifiConnection = connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            return (hasInternetCapabilities && hasValidatedCapabilities && (hasCellularConnection || hasWifiConnection))
        }
        return false
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

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        var result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        result = if (cursor == null) {
            contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex("_display_name")
            val fileName = cursor.getString(idx)
            cursor.close()

            val file = File(context.cacheDir, fileName)
            copy(context, contentURI, file)
            file.path
        }
        return result
    }

    fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) { // IOException
            e.printStackTrace()
        }
    }

    /**
     * If the form is half filled, set the progress value
     */
    fun initProgress(values: MutableMap<String, Value>, fields: ArrayList<Fields>): Int {
        val separatorCount = fields.count { it.type == Type.Separator.type }
        val progressBit = 100 / (fields.size - separatorCount)
        if (values.isNotEmpty()) {
            return progressBit * values.size
        }
        return 0
    }

    fun getDeviceCode(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * Notification configuration for upload file
     */
    fun UploadNotificationConfig(
        context: Context,
        uploadId: String,
        title: String,
        content: String
    ): UploadNotificationConfig {
        val largeIcon: Bitmap? = null
        val clearOnAction = true
        val ringToneEnabled = true

        val cancelAction = UploadNotificationAction(
            android.R.drawable.stat_notify_error,
            context.getString(R.string.upload_cancel),
            context.getCancelUploadIntent(uploadId)
        )

        val noActions = ArrayList<UploadNotificationAction>(1)
        val progressActions = ArrayList<UploadNotificationAction>(1)
        progressActions.add(cancelAction)

        val progress = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.uploading) + " " + content,
            android.R.drawable.stat_sys_upload_done,
            Color.BLUE,
            largeIcon,
            null,
            progressActions,
            clearOnAction,
            true
        )

        val success = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_success) + " " + content,
            android.R.drawable.checkbox_on_background,
            Color.GREEN,
            largeIcon,
            null,
            noActions,
            clearOnAction,
            true
        )

        val error = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_error) + " " + content,
            android.R.drawable.stat_notify_error,
            Color.RED,
            largeIcon,
            null,
            noActions,
            clearOnAction,
            false
        )

        val cancelled = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_cancel) + " " + content,
            android.R.drawable.stat_notify_error,
            Color.YELLOW,
            largeIcon,
            null,
            noActions,
            false
        )

        return UploadNotificationConfig(
            WorxApplication.notificationChannelID,
            ringToneEnabled,
            progress,
            success,
            error,
            cancelled
        )
    }
}