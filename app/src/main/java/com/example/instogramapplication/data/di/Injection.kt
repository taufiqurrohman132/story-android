package com.example.instogramapplication.data.di

import android.content.Context
import com.example.instogramapplication.DefaultResourceProvider
import com.example.instogramapplication.ResourceProvider
import com.example.instogramapplication.data.local.datastore.UserPreferences
import com.example.instogramapplication.data.local.datastore.userDataStore
import com.example.instogramapplication.data.remote.network.ApiConfig
import com.example.instogramapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository{
        val userPref = UserPreferences.getInstance(context.userDataStore)
        val user = runBlocking { userPref.getUserLoginToken().first() }
        val apiService = ApiConfig.getApiService(user)
        val resourcesProvider = DefaultResourceProvider(context)

        return UserRepository.getInstance(apiService, resourcesProvider, userPref)
    }
}