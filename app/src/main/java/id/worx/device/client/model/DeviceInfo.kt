package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class DeviceInfo (

    @SerializedName("port"               ) var port             : Int?           = null,
    @SerializedName("ip"                 ) var ip               : String?        = null,
    @SerializedName("label"              ) var label            : String?        = null,
    @SerializedName("device_no"          ) var deviceNo         : Int?           = null,
    @SerializedName("device_model"       ) var deviceModel      : String?        = null,
    @SerializedName("device_os_version"  ) var deviceOsVersion  : String?        = null,
    @SerializedName("device_app_version" ) var deviceAppVersion : String?        = null,
    @SerializedName("device_language"    ) var deviceLanguage   : String?        = null,
    @SerializedName("group_ids"          ) var groupIds         : ArrayList<Int> = arrayListOf()

)