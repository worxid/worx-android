package id.worx.device.client.util

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.SocketTimeoutException

class ConnectionTimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        return try {
            chain.proceed(chain.request())
        } catch (socket: SocketTimeoutException) {
            Response.Builder().code(408)
                .message("Request Timeout")
                .protocol(Protocol.HTTP_1_1)
                .body("".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                .request(chain.request())
                .build()
        }
    }
}