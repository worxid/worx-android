package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class BarcodeField(
    @SerializedName("barcode_type")           var barcodeType         : String? = null,
    @SerializedName("allow_manual_override")    var manuallyOverride    : Boolean? = null,
) : Fields()

data class BarcodeFieldValue(
    @SerializedName("type") override var type: String? = Type.BarcodeField.type,
    @SerializedName("value") var value : String? = ""
): Value