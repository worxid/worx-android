package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class NewTeamForm(
    @SerializedName("country") val country: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("fullname") val fullname: String? = null,
    @SerializedName("organization_name") val organization_name: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("phoneNo") val phoneNo: String? = null
)