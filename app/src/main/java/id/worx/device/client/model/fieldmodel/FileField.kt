package id.worx.device.client.model.fieldmodel

import com.google.gson.annotations.SerializedName
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Type
import id.worx.device.client.model.Value

data class FileField (

//    @SerializedName("type"               ) override var type              : String?           = null,
//    @SerializedName("id"                 ) override var id                : String?           = null,
//    @SerializedName("label"              ) override var label             : String?           = null,
//    @SerializedName("description"        ) override var description       : String?           = null,
//    @SerializedName("required"           ) override var required          : Boolean?          = null,
    @SerializedName("max_files"          ) var maxFiles          : Int?              = null,
    @SerializedName("max_file_size"      ) var maxFileSize       : Int?              = null,
    @SerializedName("min_file_size"      ) var minFileSize       : Int?              = null,
    @SerializedName("allowed_extensions" ) var allowedExtensions : ArrayList<String> = arrayListOf(),
) : Fields()

data class FileValue(
    @SerializedName("type") override var type: String? = Type.File.type,
    @SerializedName("file_ids") var value: ArrayList<Int> = arrayListOf(),
    @Transient val filePath: ArrayList<String> = arrayListOf()
) : Value

data class FilePresignedUrlResponse (
    @SerializedName("fileId" )      var fileId      : Int?    = 0,
    @SerializedName("mediaId")      var mediaId     : String? = null,
    @SerializedName("name")         var name        : String? = null,
    @SerializedName("url")          var url         : String? = null,
    @SerializedName("path")         var path        : String? = null,
    @SerializedName("mimeType")     var mimeType    : String? = null,
    @SerializedName("size")         var size        : Int?    = 0
)