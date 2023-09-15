package id.worx.mobile.data.database

import android.content.Context
import android.content.SharedPreferences
import id.worx.mobile.screen.main.AppTheme

class Session(context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    val theme get() = pref.getString(SETTING_THEME, AppTheme.DEVICE_SYSTEM.value) ?: AppTheme.DEVICE_SYSTEM.value
    val isSaveImageToGallery get() = pref.getBoolean(SETTING_SAVE_IMAGE, false)
    val latitude get() = pref.getString(LATITUDE, "-5.1966")
    val longitude get() = pref.getString(LONGITUDE, " 119.4926")
    val organization get() = pref.getString(ORGANIZATION, "")
    val organizationKey get() = pref.getString(ORGANIZATION_KEY, "")
    val deviceName get() = pref.getString(DEVICE_NAME, "")

    val serverUrl get() = pref.getString(SERVER_URL, "")

    companion object {
        const val SHARED_NAME = "id.worx"
        const val SETTING_THEME = "SETTING_THEME"
        const val SETTING_SAVE_IMAGE = "SETTING_SAVE_IMAGE"

        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val ORGANIZATION = "ORGANIZATION"
        const val ORGANIZATION_KEY = "ORGANIZATION_KEY"
        const val DEVICE_NAME = "DEVICE_NAME"

        const val SERVER_URL = "SERVER_URL"
    }

    init {
        pref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun setTheme(theme: String) {
        editor.putString(SETTING_THEME, theme)
        editor.commit()
    }

    fun isSaveImageToGallery(save: Boolean) {
        editor.putBoolean(SETTING_SAVE_IMAGE, save)
        editor.commit()
    }

    fun saveLatitude(latitude: String) {
        if (latitude.contains(",")) {
            editor.putString(LATITUDE, latitude.replace(",", "."))
        } else {
            editor.putString(LATITUDE, latitude)
        }
        editor.commit()
    }

    fun saveLongitude(longitude: String) {
        if (longitude.contains(",")) {
            editor.putString(LONGITUDE, longitude.replace(",", "."))
        } else {
            editor.putString(LONGITUDE, longitude)
        }
        editor.commit()
    }

    fun saveOrganization(org: String?) {
        editor.putString(ORGANIZATION, org)
        editor.commit()
    }

    fun saveOrganizationCode(key: String?) {
        editor.putString(ORGANIZATION_KEY, key)
        editor.commit()
    }

    fun saveDeviceName(deviceName: String) {
        editor.putString(DEVICE_NAME, deviceName)
        editor.commit()
    }

    fun saveServerUrl(url: String?) {
        editor.putString(SERVER_URL, url)
        editor.commit()
    }

}