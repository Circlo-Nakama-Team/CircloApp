package com.nakama.circlo.data.pref

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    suspend fun saveUser(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    fun getUser(): Flow<String> {
            return dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        Log.e("DataStoreManager", exception.message.toString())
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[USER_TOKEN] ?: ""
                }
        }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}