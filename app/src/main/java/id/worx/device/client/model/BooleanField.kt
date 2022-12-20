package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class BooleanField(
    var data : String? = null
): Fields()

data class BooleanValue(
    @SerializedName("type") override var type: String? = Type.Boolean.type,
    @SerializedName("value") var value: Boolean? = null
) : Value