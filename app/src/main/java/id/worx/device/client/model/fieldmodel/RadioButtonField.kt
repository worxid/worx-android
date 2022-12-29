package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class RadioButtonField (

//    @SerializedName("type"        ) override var type        : String?            = null,
//    @SerializedName("id"          ) override var id          : String?            = null,
//    @SerializedName("label"       ) override var label       : String?            = null,
//    @SerializedName("description" ) override var description : String?            = null,
//    @SerializedName("required"    ) override var required    : Boolean?           = null,
    @SerializedName("options"     ) var options     : ArrayList<Options> = arrayListOf(),

    ): Fields()

data class Options (
    @SerializedName("label" ) var label : String? = null
)

data class RadioButtonValue(
    @SerializedName("type") override var type: String? = Type.RadioGroup.type,
    @SerializedName("value_index") var value: Int? = null
) : Value