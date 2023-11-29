package id.worx.mobile.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.mobile.model.Fields
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value

data class TimeField (

//    @SerializedName("type"           ) override var type          : String?  = null,
//    @SerializedName("id"             ) override var id            : String?  = null,
//    @SerializedName("label"          ) override var label         : String?  = null,
//    @SerializedName("description"    ) override var description   : String?  = null,
//    @SerializedName("required"       ) override var required      : Boolean? = null,
    @Transient var time : String? = null,
    ) : Fields()

data class TimeValue(
    @SerializedName("type") override var type: String? = Type.Time.type,
    @SerializedName("value") var value: String? = null
) : Value