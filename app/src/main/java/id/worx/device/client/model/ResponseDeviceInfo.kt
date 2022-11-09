package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class ResponseDeviceInfo (
    @SerializedName("success" ) var success : Boolean? = null,
    @SerializedName("value"   ) var value   : DeviceValue?   = DeviceValue(),
    @SerializedName("error"   ) var error   : Error?   = null
)

data class DeviceValue (

    @SerializedName("id"                 ) var id               : Int?              = null,
    @SerializedName("label"              ) var label            : String?           = null,
    @SerializedName("ip"                 ) var ip               : String?           = null,
    @SerializedName("port"               ) var port             : Int?              = null,
    @SerializedName("groups"             ) var groups           : ArrayList<String> = arrayListOf(),
    @SerializedName("device_code"        ) var deviceCode       : String?           = null,
    @SerializedName("device_status"      ) var deviceStatus     : String?           = null,
    @SerializedName("device_model"       ) var deviceModel      : String?           = null,
    @SerializedName("device_os_version"  ) var deviceOsVersion  : String?           = null,
    @SerializedName("device_app_version" ) var deviceAppVersion : String?           = null,
    @SerializedName("device_language"    ) var deviceLanguage   : String?           = null,
    @SerializedName("joined_time"        ) var joinedTime       : String?           = null,
    @SerializedName("organization_code"  ) var organizationCode : String?           = null,
    @SerializedName("organization_name"  ) var organizationName : String?           = null

)

data class Error (

    @SerializedName("code"      ) var code      : Int?              = null,
    @SerializedName("message"   ) var message   : String?           = null,
    @SerializedName("status"    ) var status    : String?           = null,
    @SerializedName("timestamp" ) var timestamp : String?           = null,
    @SerializedName("path"      ) var path      : String?           = null,
    @SerializedName("details"   ) var details   : ArrayList<String> = arrayListOf()

)