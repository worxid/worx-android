package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class IntegerField(
    val data: String
) : Fields()

data class IntegerFieldValue(
    @SerializedName("type") override var type: String? = Type.Signature.type,
    @SerializedName("value") var value: Int? = null
) : Value