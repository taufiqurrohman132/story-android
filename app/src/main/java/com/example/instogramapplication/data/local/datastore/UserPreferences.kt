package com.example.instogramapplication.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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

    fun getUserLoginToken(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun saveUserLoginToken(token: String) =
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
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