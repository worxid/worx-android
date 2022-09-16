package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class RatingField (

//    @SerializedName("type"        ) override var type        : String?  = null,
//    @SerializedName("id"          ) override var id          : String?  = null,
//    @SerializedName("label"       ) override var label       : String?  = null,
//    @SerializedName("description" ) override var description : String?  = null,
//    @SerializedName("required"    ) override var required    : Boolean? = null,
    @SerializedName("max_stars"   ) var maxStars    : Int?     = null,

) : Fields()

data class RatingValue(
    @SerializedName("type") override var type: String? = Type.Rating.type,
    @SerializedName("value") var value: Int? = null
) : Value