package id.worx.device.client.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import id.worx.device.client.model.*

class SubmitFormTypeConverter {
    @TypeConverter
    fun fieldToJson(value: ArrayList<Fields>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToField(value: String): java.util.ArrayList<Fields> =
        Gson().fromJson(value, object : TypeToken<ArrayList<Fields>>() {}.type)

    @TypeConverter
    fun fromValueToJson(value: Map<String, Value>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToValue(json: String): Map<String, Value> {
            val gson = Gson()
            val entry = JsonParser.parseString(json).asJsonObject.entrySet().iterator().next()
            val type = entry.value.toString()
            return when  {
                type.contains(Type.TextField.type) -> gson.fromJson(json, object : TypeToken<Map<String, TextFieldValue>>() {}.type)
                type.contains(Type.Separator.type) -> gson.fromJson(json, object : TypeToken<Map<String, SeparatorValue>>() {}.type)
                type.contains(Type.Checkbox.type) -> gson.fromJson(json, object : TypeToken<Map<String, CheckBoxValue>>() {}.type)
                type.contains(Type.RadioGroup.type) -> gson.fromJson(json, object : TypeToken<Map<String, RadioButtonValue>>() {}.type)
                type.contains(Type.Rating.type) -> gson.fromJson(json, object : TypeToken<Map<String, RatingValue>>() {}.type)
                type.contains(Type.Date.type) -> gson.fromJson(json, object : TypeToken<Map<String, DateValue>>() {}.type)
                type.contains(Type.Dropdown.type) -> gson.fromJson(json, object : TypeToken<Map<String, DropDownValue>>() {}.type)
                type.contains(Type.File.type) -> gson.fromJson(json, object : TypeToken<Map<String, FileValue>>() {}.type)
                type.contains(Type.Photo.type) -> gson.fromJson(json, object : TypeToken<Map<String, ImageValue>>() {}.type)
                type.contains(Type.Signature.type) -> gson.fromJson(json, object : TypeToken<Map<String, SignatureValue>>() {}.type)
                else -> throw IllegalArgumentException("Can't deserialize ${entry.key} ${entry.value}")
            }
        }

    @TypeConverter
    fun fromSubmitLocation(value: SubmitLocation?): String = Gson().toJson(value)

    @TypeConverter
    fun toSubmitLocation(value: String): SubmitLocation =
        Gson().fromJson(value, SubmitLocation::class.java)
}