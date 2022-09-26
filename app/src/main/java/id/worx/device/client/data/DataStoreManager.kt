package id.worx.device.client.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore
    private val TAG = "Worx DataStoreManager"

    companion object {
        //String
        val LATITUDE = stringPreferencesKey("LOCATION_LATITUDE")
        val LONGITUDE = stringPreferencesKey("LOCATION_LONGITUDE")
    }

    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
            Log.d(TAG, "data store save success key : $key value: $value")
        }
    }

    suspend fun saveInt(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
            Log.d(TAG, "data store save success key : $key value: $value")
        }
    }

    suspend fun saveBool(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
            Log.d(TAG, "data store save success key : $key value: $value")
        }
    }

    suspend fun saveLong(key: String, value: Long) {
        val dataStoreKey = longPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
            Log.d(TAG, "data store save success key : $key value: $value")
        }
    }

    suspend fun read(key_pref: Preferences.Key<String>): String? {
        val preferences = dataStore.data.first()
        return preferences[key_pref]
    }

    suspend fun readInt(key_pref: Preferences.Key<Int>): Int? {
        val preferences = dataStore.data.first()
        return preferences[key_pref]
    }

    suspend fun readBool(key_pref: Preferences.Key<Boolean>): Boolean? {
        val preferences = dataStore.data.first()
        return preferences[key_pref]
    }

    suspend fun readLong(key_pref: Preferences.Key<Long>): Long? {
        val preferences = dataStore.data.first()
        return preferences[key_pref]
    }
}