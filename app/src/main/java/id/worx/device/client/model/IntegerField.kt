package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class IntegerField (
    @Transient var time : String? = null,
) : Fields()

data class IntegerValue(
    @SerializedName("type") override var type: String? = Type.Integer.type,
    @SerializedName("value") var value: Int? = null
) : Value