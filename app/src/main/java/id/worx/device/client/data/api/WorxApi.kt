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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * API communication setup
 */
interface WorxApi {

    @GET("/form/template")
    suspend fun fetchAllTemplate(): Response<ListFormResponse>

    @POST("/form/submit")
    suspend fun submitForm(@Body formFilled: SubmitForm): Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://api.dev.worx.id"

        fun create(): WorxApi {
            val logger = HttpLoggingInterceptor { Log.d("API", it) }
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
                .registerTypeAdapter(Value::class.java, ValueSerialize())
                .registerTypeAdapter(SubmitLocation::class.java, SubmitLocationSerialize())
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

class FieldsDeserializer : JsonDeserializer<Fields?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): Fields? {
        val gson = Gson()
        val entry = json!!.asJsonObject.entrySet().iterator().next()
        val type = entry.value.toString()
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
            else -> throw IllegalArgumentException("Can't deserialize ${entry.key} ${entry.value}")
        }
    }
}

class ValueSerialize: JsonSerializer<Value> {
    override fun serialize(
        src: Value?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val gson = Gson()
        return gson.toJsonTree(src)
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