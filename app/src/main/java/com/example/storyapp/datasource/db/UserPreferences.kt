package com.example.storyapp.datasource.db

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class UserPreferences (context: Context){

    private val dataStore = context.dataStore

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clear() : Boolean {
        return try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreferences? = null

        val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(context: Context) : UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context)
            }.also { INSTANCE = it }
        }
    }
}