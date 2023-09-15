package id.worx.mobile.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.mobile.model.Fields
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value

data class DateField (

//    @SerializedName("type"           ) override var type          : String?  = null,
//    @SerializedName("id"             ) override var id            : String?  = null,
//    @SerializedName("label"          ) override var label         : String?  = null,
//    @SerializedName("description"    ) override var description   : String?  = null,
//    @SerializedName("required"       ) override var required      : Boolean? = null,
    @SerializedName("disable_future" ) var disableFuture : Boolean? = null,
    @SerializedName("disable_past"   ) var disablePast   : Boolean? = null,

) : Fields()

data class DateValue(
    @SerializedName("type") override var type: String? = Type.Date.type,
    @SerializedName("value") var value: String? = null
) : Value