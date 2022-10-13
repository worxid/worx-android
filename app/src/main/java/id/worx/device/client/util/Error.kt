package id.worx.device.client.util

data class Error(
    val code: Int,
    val details: List<Any>,
    val message: String,
    val path: String,
    val status: String,
    val timestamp: String
)