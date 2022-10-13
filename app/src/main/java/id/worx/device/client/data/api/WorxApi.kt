package id.worx.device.client.data.api

import android.util.Log
import com.google.gson.*
import id.worx.device.client.model.*
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

    @POST("/mobile/forms/submit")
    suspend fun submitForm(@Body formFilled: SubmitForm): Response<ResponseBody>

    @GET("/mobile/forms/submission")
    suspend fun fetchAllSubmission(): Response<ListSubmissionResponse>

    @GET("/mobile/devices/get-info-device")
    suspend fun getDeviceInfo(): Response<ResponseDeviceInfo>

    @POST("/mobile/devices/register")
    suspend fun joinTeam(@Body joinTeamForm: JoinTeamForm) : Response<ResponseBody>

    @PUT("/mobile/devices/leave")
    suspend fun leaveTeam(@Header("deviceCode") deviceCode: String) : Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://api.dev.worx.id"

        fun create(deviceCode: String): WorxApi {
            val logger = HttpLoggingInterceptor { Log.d("API", it) }
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("deviceCode", deviceCode)
                        .addHeader("device-code", deviceCode)
                        .build()
                    chain.proceed(newRequest)
                }
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
                .registerTypeAdapter(Value::class.java, ValueSerialize())
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
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
        return when  {
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

class ValueSerialize: JsonSerializer<Value>, JsonDeserializer<Value> {
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
            else -> throw IllegalArgumentException("Can't deserialize $value")
        }
    }
}


class SubmitLocationSerialize: JsonSerializer<SubmitLocation> {
    override fun serialize(
        src: SubmitLocation?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
    }
}

class OptionsSerialize: JsonSerializer<Options> {
    override fun serialize(
        src: Options?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
    }
}