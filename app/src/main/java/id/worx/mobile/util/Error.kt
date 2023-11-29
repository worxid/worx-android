package id.worx.mobile.util

data class Error(
    val code: Int,
    val details: List<Any>,
    val message: String,
    val path: String,
    val status: String,
    val timestamp: String
)