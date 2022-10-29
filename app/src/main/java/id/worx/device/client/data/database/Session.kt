package id.worx.device.client.data.database

import android.content.Context
import android.content.SharedPreferences
import id.worx.device.client.screen.main.SettingTheme

class Session(context: Context) {
    var pref : SharedPreferences
    var editor : SharedPreferences.Editor

    val theme get() = pref.getString(SETTING_THEME, SettingTheme.System)
    val isSaveImageToGallery get() = pref.getBoolean(SETTING_SAVE_IMAGE, false)
    val latitude get() = pref.getString(LATITUDE, "-5.1966")
    val longitude get() = pref.getString(LONGITUDE, " 119.4926")
    val organization get() = pref.getString(ORGANIZATION,"")

    companion object{
        val SHARED_NAME = "id.worx"
        val SETTING_THEME = "SETTING_THEME"
        val SETTING_SAVE_IMAGE = "SETTING_SAVE_IMAGE"

        val LATITUDE = "LATITUDE"
        val LONGITUDE = "LONGITUDE"
        val ORGANIZATION = "ORGANIZATION"
    }

    init {
        pref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun setTheme(theme:String){
        editor.putString(SETTING_THEME, theme)
        editor.commit()
    }

    fun isSaveImageToGallery(save: Boolean){
        editor.putBoolean(SETTING_SAVE_IMAGE, save)
        editor.commit()
    }

    fun saveLatitude(latitude: String){
        editor.putString(LATITUDE, latitude)
        editor.commit()
    }

    fun saveLongitude(longitude: String){
        editor.putString(LONGITUDE, longitude)
        editor.commit()
    }

    fun saveOrganization(org : String){
        editor.putString(ORGANIZATION, organization)
        editor.commit()
    }

}