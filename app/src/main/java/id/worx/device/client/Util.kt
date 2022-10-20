package id.worx.device.client

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import id.worx.device.client.data.upload.CustomPlaceholdersProcessor
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Value
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.extensions.getCancelUploadIntent
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

    fun getDeviceCode(context: Context): String {
        return Settings.Secure.getString(context.contentResolver,
        Settings.Secure.ANDROID_ID)
    }

    /**
     * Notification configuration for upload file
     */
    fun UploadNotificationConfig(
        context: Context,
        uploadId: String,
        title: String
    ): UploadNotificationConfig {
        val autoClear = false
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
            context.getString(R.string.uploading),
            android.R.drawable.stat_sys_upload_done,
            Color.BLUE,
            largeIcon,
            null,
            progressActions,
            clearOnAction,
            autoClear
        )

        val success = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_success),
            android.R.drawable.checkbox_on_background,
            Color.GREEN,
            largeIcon,
            null,
            noActions,
            clearOnAction,
            autoClear
        )

        val error = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_error),
            android.R.drawable.stat_notify_error,
            Color.RED,
            largeIcon,
            null,
            noActions,
            clearOnAction,
            autoClear
        )

        val cancelled = UploadNotificationStatusConfig(
            title + ": " + CustomPlaceholdersProcessor.FILENAME_PLACEHOLDER,
            context.getString(R.string.upload_cancel),
            android.R.drawable.stat_notify_error,
            Color.YELLOW,
            largeIcon,
            null,
            noActions,
            clearOnAction
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