package id.worx.device.client.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class TimeField(
    var data: Int = 0
) : Fields()

data class TimeFieldValue(
    @SerializedName("type") override var type: String? = Type.TimeField.type,
    @SerializedName("value") var value: Int? = null,
) : Value