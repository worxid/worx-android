package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

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

    @SerializedName("fileId" ) var fileId : Int?    = null,
    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("path"   ) var path   : String? = null

)