package com.example.instogramapplication.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.userDataStore: DataStore<Preferences> by preferencesDataStore("user")

class UserPreferences private constructor(
    private val dataStore: DataStore<Preferences>
){
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val LANGUAGE = stringPreferencesKey("language")

    // sesion
    fun getUserLoginToken(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun saveUserLoginToken(token: String) =
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN] = true
        }

    fun isLoggedIn(): Flow<Boolean>{
        return dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }

    suspend fun clearSession(){
        dataStore.edit { pref ->
            pref.apply {
                remove(TOKEN_KEY)
                remove(IS_LOGGED_IN)
            }
        }
    }

    // languge
    suspend fun saveLangCode(code: String){
        dataStore.edit { prefs ->
            prefs[LANGUAGE] = code
        }
    }

    fun getLang(): Flow<String> = dataStore.data.map {
        it[LANGUAGE] ?: "id"
    }
    
    companion object{
        @Volatile
        private var INSTANCE: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            INSTANCE ?: synchronized(this){
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
    }
}