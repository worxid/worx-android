package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class BarcodeField(
    @SerializedName("barcode_type")           var barcodeType         : String? = null,
    @SerializedName("allow_manual_override")    var manuallyOverride    : Boolean? = null,
) : Fields()

data class BarcodeFieldValue(
    @SerializedName("type") override var type: String? = Type.BarcodeField.type,
    @SerializedName("value") var value : String? = ""
): Value