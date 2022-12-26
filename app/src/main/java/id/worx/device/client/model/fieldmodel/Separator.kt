package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class Separator (

//    @SerializedName("type"        ) override var type        : String? = null,
//    @SerializedName("id"          ) override var id          : String? = null,
//    @SerializedName("label"       ) override var label       : String? = null,
//    @SerializedName("description" ) override var description : String? = null,
    @SerializedName("icon"        ) var icon        : String? = null

): Fields()

data class SeparatorValue(
    @SerializedName("type") override var type: String? = Type.Separator.type
) : Value