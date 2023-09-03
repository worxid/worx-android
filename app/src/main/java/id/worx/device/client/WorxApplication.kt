package id.worx.device.client

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import id.worx.device.client.data.upload.CustomPlaceholdersProcessor
import id.worx.device.client.data.upload.GlobalRequestObserverDelegate
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.RetryPolicyConfig
import net.gotev.uploadservice.observer.request.GlobalRequestObserver
import net.gotev.uploadservice.okhttp.OkHttpStack
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// For instrumentation test
open class AppCore: Application()

@HiltAndroidApp
class WorxApplication : AppCore() {
    companion object {
        // ID of the notification channel used by upload service. This is needed by Android API 26+
        const val notificationChannelID = "UploadFileChannel"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelID,
                "UploadFile Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        initUploadInBackGround()
    }

    private fun initUploadInBackGround() {
        // Set your application namespace to avoid conflicts with other apps using this library
        UploadServiceConfig.initialize(this, notificationChannelID, BuildConfig.DEBUG)
        // Set up the Http Stack to use. If you omit this or comment it, HurlStack will be used by default
        UploadServiceConfig.httpStack = OkHttpStack(okHttpClient)
        // setup backoff multiplier
        UploadServiceConfig.retryPolicy = RetryPolicyConfig(
            initialWaitTimeSeconds = 1,
            maxWaitTimeSeconds = 10,
            multiplier = 2,
            defaultMaxRetries = 3
        )
        // you can add also your own custom placeholders to be used in notification titles and messages
        UploadServiceConfig.placeholdersProcessor = CustomPlaceholdersProcessor()
        // Uncomment to experiment Single Notification Handler
        // UploadServiceConfig.notificationHandlerFactory = { ExampleSingleNotificationHandler(it) }
        GlobalRequestObserver(this, GlobalRequestObserverDelegate())
    }

    private val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", UploadServiceConfig.defaultUserAgent)
                        .build()
                )
            }) // you can add your own request interceptors to add authorization headers.
            // do not modify the body or the http method here, as they are set and managed
            // internally by Upload Service, and tinkering with them will result in strange,
            // erroneous and unpredicted behaviors
            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .build()
                )
            }) // if you use HttpLoggingInterceptor, be sure to put it always as the last interceptor
            // in the chain and to not use BODY level logging, otherwise you will get all your
            // file contents in the log. Logging body is suitable only for small requests.
            .addInterceptor(HttpLoggingInterceptor { message: String? ->
                Log.d("OkHttp", message.orEmpty())
            }.setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .cache(null)
            .build()
}