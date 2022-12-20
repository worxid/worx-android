package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class BooleanField(
    var data : String? = null
): Fields()

data class BooleanValue(
    @SerializedName("type") override var type: String? = Type.Boolean.type,
    @SerializedName("value") var value: Boolean? = null
) : Value