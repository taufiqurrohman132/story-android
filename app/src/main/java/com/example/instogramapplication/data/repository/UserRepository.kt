package com.example.instogramapplication.data.repository

import com.example.instogramapplication.R
import com.example.instogramapplication.utils.ResourceProvider
import com.example.instogramapplication.data.local.datastore.UserPreferences
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.data.remote.network.ApiService
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val resourcesProvider: ResourceProvider,
    private val userPref: UserPreferences
) {
    companion object {

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            resourcesProvider: ResourceProvider,
            userPref: UserPreferences
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(
                apiService,
                resourcesProvider,
                userPref
            )
        }.also { instance = it }
    }

    // Karena ini request satu kali dan tidak perlu stream data berulang, maka
    suspend fun userRegister(name: String, email: String, password: String): Resource<String> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful) {
                val message = response.body()?.message.orEmpty()
                if (message.isNotBlank()) {
                    Resource.Success(message)
                } else {
                    Resource.Empty()
                }
            } else {
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(
                    response.code()
                )
                Resource.Error(message = errorMsg)
            }
        } catch (e: IOException) {
            Resource.ErrorConnection("eror koneksi")
        } catch (e: Exception) {
            Resource.Error("Error")
        }
    }

    suspend fun userLogin(email: String, password: String): Resource<String> {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                val token = response.body()?.loginResult?.token
                val userName = response.body()?.loginResult?.name
                if (!token.isNullOrEmpty() && !userName.isNullOrEmpty()) {
                    userPref.saveUserLoginToken(token, userName)
                    Resource.Success(token)
                } else {
                    Resource.Empty()
                }
            } else {
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(
                    response.code()
                )
                Resource.Error(message = errorMsg)
            }
        } catch (e: IOException) {
            Resource.ErrorConnection("eror koneksi")
        } catch (e: Exception) {
            Resource.Error("Error")
        }
    }

    suspend fun userLogout() {
        userPref.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> = userPref.isLoggedIn()

    suspend fun getUserName() =
        userPref.getUsername()

    fun getStories(): Flow<Resource<List<ListStoryItem>>> = flow {
        emit(Resource.Loading())

        try {
            val username = userPref.getUsername()
            val response = apiService.getStories()
            if (response.isSuccessful) {
                val stories = response.body()?.listStory
                if (!stories.isNullOrEmpty()) {
                    emit(Resource.Success(stories, username))
                } else {
                    emit(Resource.Empty())
                }
            } else {
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(
                    response.code()
                )
                emit(Resource.Error(errorMsg))
            }

        } catch (e: IOException) {
            emit(Resource.ErrorConnection("eror koneksi"))
        } catch (e: Exception) {
            emit(Resource.Error("Error"))
        }
    }

    suspend fun uploadStory(imageFile: File, desc: String): Resource<String> {
        return try {
            val requestBody = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.uploadStory(multipartBody, requestBody)
            if (response.isSuccessful) {
                val story = response.body()
                if (story?.error == false) {
                    Resource.Success(story.message.toString())
                } else {
                    Resource.Empty()
                }
            } else {
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(
                    response.code()
                )
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    // language
    suspend fun getCurrentLanguage(): String {
        return userPref.getLang().first()
    }

    suspend fun setLanguage(langCode: String) {
        userPref.saveLangCode(langCode)
    }

    // widget
    fun getItemWidget(): List<ListStoryItem> {
        return try {
            val response = apiService.getWidgetItems().execute()
            if (response.isSuccessful) {
                val body = response.body()
                body?.listStory ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun errorHandling(code: Int): String {
        return when (code) {
            400 -> resourcesProvider.getString(R.string.error_400)
            403 -> resourcesProvider.getString(R.string.error_403)
            404 -> resourcesProvider.getString(R.string.error_404)
            408 -> resourcesProvider.getString(R.string.error_408)
            500 -> resourcesProvider.getString(R.string.error_500)
            503 -> resourcesProvider.getString(R.string.error_503)
            else -> resourcesProvider.getString(R.string.error_else)
        }
    }
}