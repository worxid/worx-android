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
    @SerializedName("template_id") var templateId: Int? = 0,
    @SerializedName("submit_in_zone") override var submitInZone: Boolean? = true,
    @SerializedName("submit_location") var submitLocation: SubmitLocation? = SubmitLocation(),
    var status: Int = -1 //0 -> Draft, 1 -> Finish not submitted yet, 2 -> Submitted
) : BasicForm

interface Value{
    var type      : String?
}

data class SubmitLocation(
    @SerializedName("address") var address: String? = "",
    @SerializedName("lat") var lat: Int? = 0,
    @SerializedName("lng") var lng: Int? = 0
)

