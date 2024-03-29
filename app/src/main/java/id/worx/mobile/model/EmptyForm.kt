package id.worx.mobile.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

enum class Type(val type: String) {
    TextField("text"),
    Checkbox("checkbox_group"),
    Dropdown("dropdown"),
    RadioGroup("radio_group"),
    Date("date"),
    Rating("rating"),
    File("file"),
    Photo("photo"),
    Signature("signature"),
    Separator("separator"),
    Time("time"),
    Boolean("boolean"),
    Integer("integer"),
    BarcodeField("barcode"),
    Sketch("sketch")
}

@Entity(tableName = "form")
data class EmptyForm(
    @PrimaryKey
    @SerializedName("id") override var id: Int? = null,
    @SerializedName("label") override var label: String? = null,
    @SerializedName("description") override var description: String? = null,
    @SerializedName("fields") override var fields: ArrayList<Fields> = arrayListOf(),
    @SerializedName("submit_in_zone") override var submitInZone: Boolean? = null,
    @SerializedName("default") var default: Boolean? = null,
    @SerializedName("created_on") override var created: String? = null,
    @SerializedName("modified_on") override var lastUpdated: String? = null,
    @SerializedName("fields_size") var fieldsSize: Int? = null,
) : BasicForm

sealed interface BasicForm {
    var id: Int?
    var label: String?
    var description: String?
    var fields: ArrayList<Fields>
    var submitInZone: Boolean?
    var created: String?
    var lastUpdated: String?
}

abstract class Fields(
    @SerializedName("id") open var id: String? = null,
    @SerializedName("label") open var label: String? = null,
    @SerializedName("description") open var description: String? = null,
    @SerializedName("type") open var type: String? = null,
    @SerializedName("required") open var required: Boolean? = null,
    @Transient open var isValid: Boolean? = null,
)

data class Group(
    @SerializedName("label") var label: String? = null
)

enum class FormSortType(val value: String) {
    NAME("Name"),
    DESCRIPTION("Description"),
    LAST_MODIFIED("Last Modified"),
}

enum class FormSortOrderBy {
    ASC,
    DESC
}

data class FormSortModel(
    val formSortType: FormSortType = FormSortType.NAME,
    val formSortOrderBy: FormSortOrderBy = FormSortOrderBy.ASC
) {
    fun toggleSortOrderBy(): FormSortModel {
        val newSortOrderBy = if (formSortOrderBy == FormSortOrderBy.ASC) {
            FormSortOrderBy.DESC
        } else {
            FormSortOrderBy.ASC
        }
        return FormSortModel(formSortType, newSortOrderBy)
    }

    fun getSortQuery(): String {
        return "${getSortField()} ${formSortOrderBy.name}"
    }

    private fun getSortField(): String {
        return when (formSortType) {
            FormSortType.NAME -> LABEL
            FormSortType.DESCRIPTION -> DESCRIPTION
            FormSortType.LAST_MODIFIED -> LAST_UPDATED
        }
    }

    companion object {
        private const val LABEL = "label"
        private const val DESCRIPTION = "description"
        private const val LAST_UPDATED = "lastUpdated"
    }
}