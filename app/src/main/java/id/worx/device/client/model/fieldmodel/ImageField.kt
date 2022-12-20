package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class ImageField (

//    @SerializedName("type"                 ) override var type               : String?  = null,
//    @SerializedName("id"                   ) override var id                 : String?  = null,
//    @SerializedName("label"                ) override var label              : String?  = null,
//    @SerializedName("description"          ) override var description        : String?  = null,
//    @SerializedName("required"             ) override var required           : Boolean? = null,
    @SerializedName("max_files"            ) var maxFiles           : Int?     = null,
    @SerializedName("allow_gallery_upload" ) var allowGalleryUpload : Boolean? = null,

) : Fields()

data class ImageValue(
    @SerializedName("type") override var type: String? = Type.Photo.type,
    @SerializedName("file_ids") var value: ArrayList<Int> = arrayListOf(),
    @Transient val filePath: ArrayList<String> = arrayListOf()
) : Value