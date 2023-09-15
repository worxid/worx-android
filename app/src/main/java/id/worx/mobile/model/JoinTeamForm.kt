package id.worx.mobile.model

import com.google.gson.annotations.SerializedName

data class JoinTeamForm(
    @SerializedName("device_app_version") val device_app_version: String? = null,
    @SerializedName("device_code") val device_code: String? = null,
    @SerializedName("device_language") val device_language: String? = null,
    @SerializedName("device_model") val device_model: String? = null,
    @SerializedName("device_os_version") val device_os_version: String? = null,
    @SerializedName("ip") val ip: String? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("organization_code") val organization_code: String? = null,
    @SerializedName("port") val port: Int? = 0
)