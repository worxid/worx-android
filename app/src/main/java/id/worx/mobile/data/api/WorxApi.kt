package id.worx.mobile.data.api

import android.util.Log
import com.google.gson.*
import id.worx.mobile.model.*
import id.worx.mobile.model.fieldmodel.*
import id.worx.mobile.util.ConnectionTimeoutInterceptor
import id.worx.mobile.model.DeviceInfo
import id.worx.mobile.model.Fields
import id.worx.mobile.model.IntegerField
import id.worx.mobile.model.IntegerValue
import id.worx.mobile.model.JoinTeamForm
import id.worx.mobile.model.ListFormResponse
import id.worx.mobile.model.ListSubmissionResponse
import id.worx.mobile.model.NewTeamForm
import id.worx.mobile.model.ResponseDeviceInfo
import id.worx.mobile.model.SubmitForm
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
import id.worx.mobile.model.fieldmodel.FilePresignedUrlResponse
import id.worx.mobile.model.fieldmodel.FileValue
import id.worx.mobile.model.fieldmodel.ImageField
import id.worx.mobile.model.fieldmodel.ImageValue
import id.worx.mobile.model.fieldmodel.Options
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
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * API communication setup
 */
interface WorxApi {

    @GET("/mobile/forms")
    suspend fun fetchAllTemplate(): Response<ListFormResponse>

    @GET("/mobile/media/presigned-url")
    suspend fun getPresignedUrl(@Query("filename") fileName: String): Response<FilePresignedUrlResponse>

    @POST("/mobile/forms/submit")
    suspend fun submitForm(@Body formFilled: SubmitForm): Response<ResponseBody>

    @GET("/mobile/forms/submissions")
    suspend fun fetchAllSubmission(): Response<ListSubmissionResponse>

    @GET("/mobile/devices/get-info-device")
    suspend fun getDeviceInfo(): Response<ResponseDeviceInfo>

    @POST("/mobile/devices/register")
    suspend fun joinTeam(@Body joinTeamForm: JoinTeamForm) : Response<ResponseBody>

    @PUT("/mobile/devices/leave")
    suspend fun leaveTeam() : Response<ResponseBody>

    @POST("/mobile/users/create-new-team")
    suspend fun createNewTeam(@Body newTeamForm: NewTeamForm) : Response<ResponseDeviceInfo>

    @PUT("mobile/devices/update-info")
    suspend fun updateDeviceInfo(@Body deviceInfo: DeviceInfo) : Response<ResponseBody>

    companion object {
        fun create(deviceCode: String, baseUrl: String): WorxApi {
            val logger = HttpLoggingInterceptor { Log.d("WORX-API", it) }
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("deviceCode", deviceCode)
                        .build()
                    chain.proceed(newRequest)
                }
                .addInterceptor(logger)
                .addInterceptor(ConnectionTimeoutInterceptor())
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
                .registerTypeAdapter(Value::class.java, ValueSerialize())
                .create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(WorxApi::class.java)
        }
    }
}

class FieldsDeserializer : JsonDeserializer<Fields?>, JsonSerializer<Fields?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): Fields? {
        val gson = Gson()
        val entry = json!!.asJsonObject
        val type = entry.get("type").toString()
        return when {
            type.contains(Type.TextField.type) -> gson.fromJson(json, TextField::class.java)
            type.contains(Type.Separator.type) -> gson.fromJson(json, Separator::class.java)
            type.contains(Type.Checkbox.type) -> gson.fromJson(json, CheckBoxField::class.java)
            type.contains(Type.RadioGroup.type) -> gson.fromJson(json, RadioButtonField::class.java)
            type.contains(Type.Rating.type) -> gson.fromJson(json, RatingField::class.java)
            type.contains(Type.Date.type) -> gson.fromJson(json, DateField::class.java)
            type.contains(Type.Dropdown.type) -> gson.fromJson(json, DropDownField::class.java)
            type.contains(Type.File.type) -> gson.fromJson(json, FileField::class.java)
            type.contains(Type.Photo.type) -> gson.fromJson(json, ImageField::class.java)
            type.contains(Type.Signature.type) -> gson.fromJson(json, SignatureField::class.java)
            type.contains(Type.Time.type) -> gson.fromJson(json, TimeField::class.java)
            type.contains(Type.Boolean.type) -> gson.fromJson(json, BooleanField::class.java)
            type.contains(Type.Integer.type) -> gson.fromJson(json, IntegerField::class.java)
            type.contains(Type.BarcodeField.type) -> gson.fromJson(json, BarcodeField::class.java)
            type.contains(Type.Sketch.type) -> gson.fromJson(json, SketchField::class.java)
            else -> throw IllegalArgumentException("Can't deserialize $entry ")
        }
    }

    override fun serialize(
        src: Fields?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return Gson().toJsonTree(src)
    }
}

class ValueSerialize : JsonSerializer<Value>, JsonDeserializer<Value> {
    override fun serialize(
        src: Value?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): Value {
        val gson = Gson()
        val value = gson.fromJson(json!!.asJsonObject, SeparatorValue::class.java)
        val type = value.type ?: ""
        return when {
            type.contains(Type.TextField.type) ->
                gson.fromJson(json, TextFieldValue::class.java)
            type.contains(Type.Separator.type) ->
                gson.fromJson(json, SeparatorValue::class.java)
            type.contains(Type.Checkbox.type) ->
                gson.fromJson(json, CheckBoxValue::class.java)
            type.contains(Type.RadioGroup.type) ->
                gson.fromJson(json, RadioButtonValue::class.java)
            type.contains(Type.Rating.type) ->
                gson.fromJson(json, RatingValue::class.java)
            type.contains(Type.Date.type) ->
                gson.fromJson(json, DateValue::class.java)
            type.contains(Type.Dropdown.type) ->
                gson.fromJson(json, DropDownValue::class.java)
            type.contains(Type.File.type) ->
                gson.fromJson(json, FileValue::class.java)
            type.contains(Type.Photo.type) ->
                gson.fromJson(json, ImageValue::class.java)
            type.contains(Type.Signature.type) ->
                gson.fromJson(json, SignatureValue::class.java)
            type.contains(Type.Time.type) ->
                gson.fromJson(json, TimeValue::class.java)
            type.contains(Type.Integer.type) ->
                gson.fromJson(json, IntegerValue::class.java)
            type.contains(Type.Boolean.type) ->
                gson.fromJson(json, BooleanValue::class.java)
            type.contains(Type.BarcodeField.type) ->
                gson.fromJson(json, BarcodeFieldValue::class.java)
            type.contains(Type.Sketch.type) ->
                gson.fromJson(json, SketchValue::class.java)
            else -> throw IllegalArgumentException("Can't deserialize $value")
        }
    }
}


class SubmitLocationSerialize : JsonSerializer<SubmitLocation> {
    override fun serialize(
        src: SubmitLocation?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
    }
}

class OptionsSerialize : JsonSerializer<Options> {
    override fun serialize(
        src: Options?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
    }
}