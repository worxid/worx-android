package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class TextField (

    //@SerializedName("type"        ) override var type        : String?  = null,
    //@SerializedName("id"          ) override var id          : String?  = null,
    //@SerializedName("label"       ) override var label       : String?  = null,
    //@SerializedName("required"    ) override var required    : Boolean? = null,
    //@SerializedName("description" ) override var description : String?  = null,
    @SerializedName("min_length"  ) var minLength   : Int?     = null,
    @SerializedName("max_length"  ) var maxLength   : Int?     = null
) : Fields()


data class TextFieldValue(
    @SerializedName("type") override var type: String? = Type.TextField.type,
    @SerializedName("value") var values: String? = null
) : Value