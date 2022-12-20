package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Group
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class CheckBoxField (

//    @SerializedName("type"        ) override var type        : String?          = null,
//    @SerializedName("id"          ) override var id          : String?          = null,
//    @SerializedName("label"       ) override var label       : String?          = null,
//    @SerializedName("description" ) override var description : String?          = null,
//    @SerializedName("required"    ) override var required    : Boolean?         = null,
    @SerializedName("group"       ) var group       : ArrayList<Group> = arrayListOf(),
    @SerializedName("min_checked") var minChecked: Int? = null,
    @SerializedName("max_checked") var maxChecked: Int? = null
): Fields()

data class CheckBoxValue(
    @SerializedName("type") override var type: String? = Type.Checkbox.type,
    @SerializedName("values") var value: ArrayList<Boolean> = arrayListOf()
) : Value