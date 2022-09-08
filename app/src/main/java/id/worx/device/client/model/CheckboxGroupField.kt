package id.worx.device.client.model

import com.google.gson.annotations.SerializedName

data class CheckboxGroupField(
    var id: String? = null,
    var label: String? = null,
    var description: String? = null,
    var fieldType: Type? = null,
    var required: Boolean = false,
    @SerializedName("min_checked")
    var minChecked: Int = 0,
    @SerializedName("max_checked")
    var maxChecked: Int = 0,
    var group: List<Option> = listOf<Option>(),
) : Field(id, label, description, fieldType, required) {
}