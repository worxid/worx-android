package id.worx.device.client.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class SketchField(
    var emptyProperty: String? = null
) : Fields()

data class SketchValue(
    @SerializedName("type") override var type: String? = Type.Sketch.type,
    @SerializedName("file_id") var value: Int? = null,
    @Transient var bitmap: Bitmap? = null
) : Value