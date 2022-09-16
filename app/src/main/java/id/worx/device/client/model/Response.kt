package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class ListFormResponse (
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("list"    ) var list    : ArrayList<EmptyForm> = arrayListOf()
)