package id.worx.device.client.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class SignatureField(

//    @SerializedName("type"        ) override var type        : String?  = null,
//    @SerializedName("id"          ) override var id          : String?  = null,
//    @SerializedName("label"       ) override var label       : String?  = null,
//    @SerializedName("description" ) override var description : String?  = null,
//    @SerializedName("required"    ) override var required    : Boolean? = null,
    var emptyProperty : String? = null
)
 : Fields ()

data class SignatureValue(
    @SerializedName("type") override var type: String? = Type.Signature.type,
    @SerializedName("file_id") var value: Int? = null,
    @Transient var bitmap: Bitmap? = null
) : Value