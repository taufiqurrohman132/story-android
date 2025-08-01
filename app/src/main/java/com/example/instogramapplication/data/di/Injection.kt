package com.example.instogramapplication.data.di

import android.content.Context
import com.example.instogramapplication.utils.DefaultResourceProvider
import com.example.instogramapplication.data.local.datastore.UserPreferences
import com.example.instogramapplication.data.local.datastore.userDataStore
import com.example.instogramapplication.data.remote.network.ApiConfig
import com.example.instogramapplication.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPref = UserPreferences.getInstance(context.userDataStore)
        val apiService = ApiConfig.getApiService(userPref)
        val resourcesProvider = DefaultResourceProvider(context)

        return UserRepository.getInstance(apiService, resourcesProvider, userPref)
    }
}