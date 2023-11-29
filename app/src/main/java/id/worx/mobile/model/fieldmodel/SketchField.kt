package id.worx.mobile.model.fieldmodel

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import id.worx.mobile.model.Fields
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value

data class SketchField(
    var emptyProperty: String? = null
) : Fields()

data class SketchValue(
    @SerializedName("type") override var type: String? = Type.Sketch.type,
    @SerializedName("file_id") var value: Int? = null,
    @Transient var bitmap: Bitmap? = null
) : Value