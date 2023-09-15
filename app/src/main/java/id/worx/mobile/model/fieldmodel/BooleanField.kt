package id.worx.mobile.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.mobile.model.Fields
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value

data class BooleanField(
    var data : String? = null
): Fields()

data class BooleanValue(
    @SerializedName("type") override var type: String? = Type.Boolean.type,
    @SerializedName("value") var value: Boolean? = null
) : Value