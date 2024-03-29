package id.worx.mobile.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.mobile.model.Fields
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value

data class TextField(

    //@SerializedName("type"        ) override var type        : String?  = null,
    //@SerializedName("id"          ) override var id          : String?  = null,
    //@SerializedName("label"       ) override var label       : String?  = null,
    //@SerializedName("required"    ) override var required    : Boolean? = null,
    //@SerializedName("description" ) override var description : String?  = null,
    @SerializedName("min_length") var minLength: Int? = null,
    @SerializedName("max_length") var maxLength: Int? = null,
    @SerializedName("allow_multi_lines") var allowMultiline: Boolean? = null,
) : Fields()


data class TextFieldValue(
    @SerializedName("type") override var type: String? = Type.TextField.type,
    @SerializedName("value") var values: String? = null,
) : Value