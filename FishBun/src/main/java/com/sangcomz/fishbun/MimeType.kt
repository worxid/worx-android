package com.sangcomz.fishbun

enum class MimeType(val type: String) {
    GIF("image/gif"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    MP4("video/*"),
    BMP("image,bmp"),
    WEBP("image/webp");
}