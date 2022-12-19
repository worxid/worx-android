package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

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
    @SerializedName("value") var value: id.worx.device.client.model.LocalTime? = null
) : Value

data class LocalTime(
    @SerializedName("hour") var hour: Int = 0,
    @SerializedName("minute") var minute: Int = 0,
    @SerializedName("second") var second: Int = 0
)