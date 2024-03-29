package id.worx.mobile.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonParser
import id.worx.mobile.model.Attachment
import id.worx.mobile.model.Fields
import id.worx.mobile.model.IntegerField
import id.worx.mobile.model.IntegerValue
import id.worx.mobile.model.Source
import id.worx.mobile.model.SubmitLocation
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value
import id.worx.mobile.model.fieldmodel.BarcodeField
import id.worx.mobile.model.fieldmodel.BarcodeFieldValue
import id.worx.mobile.model.fieldmodel.BooleanField
import id.worx.mobile.model.fieldmodel.BooleanValue
import id.worx.mobile.model.fieldmodel.CheckBoxField
import id.worx.mobile.model.fieldmodel.CheckBoxValue
import id.worx.mobile.model.fieldmodel.DateField
import id.worx.mobile.model.fieldmodel.DateValue
import id.worx.mobile.model.fieldmodel.DropDownField
import id.worx.mobile.model.fieldmodel.DropDownValue
import id.worx.mobile.model.fieldmodel.FileField
import id.worx.mobile.model.fieldmodel.FileValue
import id.worx.mobile.model.fieldmodel.ImageField
import id.worx.mobile.model.fieldmodel.ImageValue
import id.worx.mobile.model.fieldmodel.RadioButtonField
import id.worx.mobile.model.fieldmodel.RadioButtonValue
import id.worx.mobile.model.fieldmodel.RatingField
import id.worx.mobile.model.fieldmodel.RatingValue
import id.worx.mobile.model.fieldmodel.Separator
import id.worx.mobile.model.fieldmodel.SeparatorValue
import id.worx.mobile.model.fieldmodel.SignatureField
import id.worx.mobile.model.fieldmodel.SignatureValue
import id.worx.mobile.model.fieldmodel.SketchField
import id.worx.mobile.model.fieldmodel.SketchValue
import id.worx.mobile.model.fieldmodel.TextField
import id.worx.mobile.model.fieldmodel.TextFieldValue
import id.worx.mobile.model.fieldmodel.TimeField
import id.worx.mobile.model.fieldmodel.TimeValue


class SubmitFormTypeConverter {
    @TypeConverter
    fun sourceToJson(value: Source?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToSource(value: String): Source = Gson().fromJson(value, Source::class.java)

    @TypeConverter
    fun fromAttachments(value: ArrayList<Attachment>?): String = Gson().toJson(value)

    @TypeConverter
    fun toAttachments(value: String?) : ArrayList<Attachment>? {
        var returnAttachment : ArrayList<Attachment>? = null
        if (!value.equals("null")){
            returnAttachment = arrayListOf()
            val array = JsonParser.parseString(value).asJsonArray
            array.forEach { jsonElement ->
                val jsonObject = jsonElement.asJsonObject
                val name = jsonObject.get("name").toString()
                val path = jsonObject.get("path").toString()
                val fileId = jsonObject.get("file_id").toString().toInt()
                val mediaId = jsonObject.get("media_id").toString()
                val attachment = Attachment(name, path, fileId,mediaId)
                returnAttachment.add(attachment)
            }
        }
        return returnAttachment
    }

    @TypeConverter
    fun fieldToJson(value: ArrayList<Fields>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToField(value: String): java.util.ArrayList<Fields> {
        val gson = Gson()
        val returnArray = arrayListOf<Fields>()
        val array = JsonParser.parseString(value).asJsonArray
        array.forEach { jsonElement ->
            val entry = jsonElement.asJsonObject
            val type = entry.get("type").toString()
            when {
                type.contains(Type.TextField.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        TextField::class.java
                    )
                )
                type.contains(Type.Separator.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        Separator::class.java
                    )
                )
                type.contains(Type.Checkbox.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        CheckBoxField::class.java
                    )
                )
                type.contains(Type.RadioGroup.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        RadioButtonField::class.java
                    )
                )
                type.contains(Type.Rating.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        RatingField::class.java
                    )
                )
                type.contains(Type.Date.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        DateField::class.java
                    )
                )
                type.contains(Type.Dropdown.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        DropDownField::class.java
                    )
                )
                type.contains(Type.File.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        FileField::class.java
                    )
                )
                type.contains(Type.Photo.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        ImageField::class.java
                    )
                )
                type.contains(Type.Signature.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        SignatureField::class.java
                    )
                )
                type.contains(Type.Time.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        TimeField::class.java
                    )
                )
                type.contains(Type.Boolean.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        BooleanField::class.java
                    )
                )
                type.contains(Type.Integer.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        IntegerField::class.java
                    )
                )
                type.contains(Type.BarcodeField.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        BarcodeField::class.java
                    )
                )
                type.contains(Type.Sketch.type) -> returnArray.add(
                    gson.fromJson(
                        jsonElement,
                        SketchField::class.java
                    )
                )
                else -> throw IllegalArgumentException("Can't deserialize $entry")
            }
        }
        return returnArray
    }

    @TypeConverter
    fun fromValueToJson(value: Map<String, Value>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToValue(json: String): Map<String, Value> {
        val returnMap = mutableMapOf<String, Value>()
        val jsonObject = JsonParser.parseString(json).asJsonObject
        val gson = Gson()
        jsonObject.entrySet().forEach { map ->
            val value = gson.fromJson(map.value, SeparatorValue::class.java)
            val type = value.type ?: ""
            when  {
                type.contains(Type.TextField.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, TextFieldValue::class.java)
                type.contains(Type.Separator.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, SeparatorValue::class.java)
                type.contains(Type.Checkbox.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, CheckBoxValue::class.java)
                type.contains(Type.RadioGroup.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, RadioButtonValue::class.java)
                type.contains(Type.Rating.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, RatingValue::class.java)
                type.contains(Type.Date.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, DateValue::class.java)
                type.contains(Type.Dropdown.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, DropDownValue::class.java)
                type.contains(Type.File.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, FileValue::class.java)
                type.contains(Type.Photo.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, ImageValue::class.java)
                type.contains(Type.Signature.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, SignatureValue::class.java)
                type.contains(Type.Time.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, TimeValue::class.java)
                type.contains(Type.Integer.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, IntegerValue::class.java)
                type.contains(Type.Boolean.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, BooleanValue::class.java)
                type.contains(Type.BarcodeField.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, BarcodeFieldValue::class.java)
                type.contains(Type.Sketch.type) -> returnMap[map.key] =
                    gson.fromJson(map.value, SketchValue::class.java)
                else -> throw IllegalArgumentException("Can't deserialize ${map.key} ${map.value}")
                 }
        }
        return returnMap
        }

    @TypeConverter
    fun fromSubmitLocation(value: SubmitLocation?): String = Gson().toJson(value)

    @TypeConverter
    fun toSubmitLocation(value: String): SubmitLocation =
        Gson().fromJson(value, SubmitLocation::class.java)
}