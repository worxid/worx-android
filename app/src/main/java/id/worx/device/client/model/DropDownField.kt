package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class DropDownField (

//    @SerializedName("type"        ) override var type        : String?            = null,
//    @SerializedName("id"          ) override var id          : String?            = null,
//    @SerializedName("label"       ) override var label       : String?            = null,
//    @SerializedName("description" ) override var description : String?            = null,
//    @SerializedName("required"    ) override var required    : Boolean?           = null,
    @SerializedName("options"     ) var options     : ArrayList<Options> = arrayListOf(),
) : Fields()

data class DropDownValue(
    @SerializedName("type") override var type: String? = Type.Dropdown.type,
    @SerializedName("value_index") var value: Int? = null
) : Value