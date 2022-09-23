package id.worx.device.client.data.database

import android.content.Context
import android.content.SharedPreferences

class Session(context: Context) {
    var pref : SharedPreferences
    var editor : SharedPreferences.Editor

    val theme get() = pref.getString(SETTING_THEME, "")
    val isSaveImageToGallery get() = pref.getBoolean(SETTING_SAVE_IMAGE, false)

    companion object{
        val SHARED_NAME = "id.worx"
        val SETTING_THEME = "SETTING_THEME"
        val SETTING_SAVE_IMAGE = "SETTING_SAVE_IMAGE"
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

}