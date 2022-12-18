package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class BarcodeField(
    @SerializedName("is_1d_only")           var is1DBarcode         : Boolean? = null,
    @SerializedName("manually_override")    var manuallyOverride    : Boolean? = null,
) : Fields()

data class BarcodeFieldValue(
    @SerializedName("type") override var type: String? = Type.TextField.type,
    @SerializedName("value") var value : String? = "",
    @Transient val filePath: ArrayList<String> = arrayListOf()
): Value