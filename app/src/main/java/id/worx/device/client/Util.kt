package id.worx.device.client

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import id.worx.device.client.data.upload.CustomPlaceholdersProcessor
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Value
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URLConnection
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

    fun getFileFromUri(contentResolver: ContentResolver, uri: Uri, directory: File): File {
        val source = contentResolver.openInputStream(uri)
        val fileType = URLConnection.guessContentTypeFromStream(source)
        val file = File.createTempFile(uri.path ?: "file", "", directory)
        file.outputStream().use {
            source?.copyTo(it)
        }
        source?.close()
        return file
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        var result = ""
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex("_display_name")
            val id2 = cursor.getColumnIndex("document_id")
            val id3 = cursor.getColumnIndex("mime_type")
            val fileName = cursor.getString(idx)
            cursor.close()

            val file = File(context.cacheDir, fileName)
            copy(context, contentURI, file)
            result = file.path
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