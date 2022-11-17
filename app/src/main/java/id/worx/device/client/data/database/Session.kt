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
    val organizationKey get() = pref.getString(ORGANIZATION_KEY, "")
    val deviceName get() = pref.getString(DEVICE_NAME, "")

    companion object{
        val SHARED_NAME = "id.worx"
        val SETTING_THEME = "SETTING_THEME"
        val SETTING_SAVE_IMAGE = "SETTING_SAVE_IMAGE"

        val LATITUDE = "LATITUDE"
        val LONGITUDE = "LONGITUDE"
        val ORGANIZATION = "ORGANIZATION"
        val ORGANIZATION_KEY = "ORGANIZATION_KEY"
        val DEVICE_NAME = "DEVICE_NAME"
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
        if (latitude.contains(",")) {
            editor.putString(LATITUDE, latitude.replace(",","."))
        } else {
            editor.putString(LATITUDE, latitude)
        }
        editor.commit()
    }

    fun saveLongitude(longitude: String){
        if (longitude.contains(",")){
            editor.putString(LONGITUDE, longitude.replace(",","."))
        } else {
            editor.putString(LONGITUDE, longitude)
        }
        editor.commit()
    }

    fun saveOrganization(org : String?){
        editor.putString(ORGANIZATION, org)
        editor.commit()
    }

    fun saveOrganizationCode(key : String?){
        editor.putString(ORGANIZATION_KEY, key)
        editor.commit()
    }

    fun saveDeviceName(deviceName : String){
        editor.putString(DEVICE_NAME, deviceName)
        editor.commit()
    }

}