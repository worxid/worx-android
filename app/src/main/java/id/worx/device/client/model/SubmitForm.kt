package id.worx.device.client.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "submit_form")
data class SubmitForm(
    @PrimaryKey(autoGenerate = true) var dbId: Int? = null,
    @SerializedName("id") override var id: Int? = null,
    @SerializedName("label") override var label: String? = null,
    @SerializedName("description") override var description: String? = null,
    @SerializedName("fields") override var fields: ArrayList<Fields> = arrayListOf(),
    @SerializedName("values") var values: Map<String, Value> = mapOf(),
    @SerializedName("source") var source: Source? = Source(),
    @SerializedName("attachments") var attachments : ArrayList<Attachment>? = arrayListOf(),
    @SerializedName("template_id") var templateId: Int? = 0,
    @SerializedName("submit_in_zone") override var submitInZone: Boolean? = true,
    @SerializedName("submit_location") var submitLocation: SubmitLocation? = SubmitLocation(),
    @SerializedName("created_on"     ) override var created      : String?           = null,
    @SerializedName("modified_on"    ) override var lastUpdated     : String?           = null,
    @SerializedName("submit_date"    ) var submitDate     : String?           = null,
    var status: Int = 2, //0 -> Draft, 1 -> Finish not submitted yet, 2 -> Submitted
) : BasicForm

@Entity(tableName = "draft_form")
data class DraftForm(
    @PrimaryKey(autoGenerate = true) var dbId: Int? = null,
    @SerializedName("id") override var id: Int? = null,
    @SerializedName("label") override var label: String? = null,
    @SerializedName("description") override var description: String? = null,
    @SerializedName("fields") override var fields: ArrayList<Fields> = arrayListOf(),
    @SerializedName("values") var values: Map<String, Value> = mapOf(),
    @SerializedName("source") var source: Source? = Source(),
    @SerializedName("attachments") var attachments : ArrayList<Attachment>? = arrayListOf(),
    @SerializedName("template_id") var templateId: Int? = 0,
    @SerializedName("submit_in_zone") override var submitInZone: Boolean? = true,
    @SerializedName("submit_location") var submitLocation: SubmitLocation? = SubmitLocation(),
    @SerializedName("created_on"     ) override var created      : String?           = null,
    @SerializedName("modified_on"    ) override var lastUpdated     : String?           = null,
    @SerializedName("submit_date"    ) var submitDate     : String?           = null,
    var status: Int = 2, //0 -> Draft, 1 -> Finish not submitted yet, 2 -> Submitted
) : BasicForm

interface Value{
    var type      : String?
}

data class SubmitLocation(
    @SerializedName("address") var address: String? = "",
    @SerializedName("lat") var lat: Double? = 0.0,
    @SerializedName("lng") var lng: Double? = 0.0
)

data class Source(
    @SerializedName("type")     var type: String? = "",
    @SerializedName("label")    var label: String? = "",
)

data class Attachment(
    @SerializedName("name")     var name: String? = null,
    @SerializedName("path")     var path: String? = null,
    @SerializedName("file_id")  var fileId: Int? = 0,
    @SerializedName("media_id") var mediaId: String? = null,
)