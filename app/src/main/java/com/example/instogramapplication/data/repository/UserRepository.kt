package com.example.instogramapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.instogramapplication.R
import com.example.instogramapplication.ResourceProvider
import com.example.instogramapplication.data.local.datastore.UserPreferences
import com.example.instogramapplication.data.remote.model.FileUploadResponse
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.data.remote.network.ApiService
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.Resource
import com.yariksoffice.lingver.Lingver
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
){
    companion object{
        val TAG = UserRepository::class.java.simpleName

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            resourcesProvider: ResourceProvider,
            userPref: UserPreferences
        ): UserRepository = instance ?: synchronized(this){
            Log.d(TAG, "getInstance: inisialize repository pertama")
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
            Log.d(TAG, "userRegister: respons register $response")
            if (response.isSuccessful){
                val message = response.body()?.message.orEmpty()
                Log.d(TAG, "userRegister: message $message")
                if (message.isNotBlank()){
                    Resource.Success(message)
                }else{
                    Resource.Empty()
                }
            }else{
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(response.code())
                Log.e(TAG, "userRegister: error notSuccess ${response.code()} message : $errorMsg")
                Resource.Error(message = errorMsg)
            }
        }catch (e: IOException){
            Resource.ErrorConnection("eror koneksi")
        }catch (e: Exception){
            Log.e(TAG, "userRegister: error exception ", e)
            Resource.Error("Error")
        }
    }
    suspend fun userLogin(email: String, password: String): Resource<String>{
        return try {
            val response = apiService.login(email, password)
            Log.d(TAG, "userLogin: respons login $response")
            if (response.isSuccessful){
                val token = response.body()?.loginResult?.token
                val userName = response.body()?.loginResult?.name
                Log.d(TAG, "userLogin: message $token")
                if (!token.isNullOrEmpty() && !userName.isNullOrEmpty()){
                    userPref.saveUserLoginToken(token, userName)
                    Resource.Success(token)
                }else{
                    Resource.Empty()
                }
            }else{
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(response.code())
                Log.e(TAG, "userLogin: error notSuccess ${response.code()} message : $errorMsg")
                Resource.Error(message = errorMsg)
            }
        }catch (e: IOException){
            Resource.ErrorConnection("eror koneksi")
        }catch (e: Exception){
            Log.e(TAG, "userLogin: error exception ", e)
            Resource.Error("Error")
        }
    }

    suspend fun getUserName() =
        userPref.getUsername()

    suspend fun userLogout(){
        userPref.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> = userPref.isLoggedIn()

    fun getStories(): Flow<Resource<List<ListStoryItem>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getStories()
            Log.d(TAG, "getStories: respons $response")
            if (response.isSuccessful){
                val stories = response.body()?.listStory
                Log.d(TAG, "getStories: body $stories")
                if (!stories.isNullOrEmpty()){
                    emit(Resource.Success(stories))
                }else{
                    emit(Resource.Empty())
                }
            }else{
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(response.code())
                Log.e(TAG, "getStories: error notSuccess ${response.code()} message : $errorMsg")
                emit(Resource.Error(errorMsg))
            }

        }catch (e: IOException){
            emit(Resource.ErrorConnection("eror koneksi"))
        }catch (e: Exception){
            Log.e(TAG, "getStories: error exception ", e )
            emit(Resource.Error("Error"))
        }
    }

//    suspend fun getDetailStory(id: String): Resource<Story> {
//        Resource.Loading<Story>()
//
//        return try {
//            val response = apiService.getDetailStory(id)
//            Log.d(TAG, "getDetailStory: respons $response")
//            if (response.isSuccessful){
//                val story = response.body()?.story
//                if (story != null){
//                    Resource.Success(story)
//                }else{
//                    Resource.Empty()
//                }
//            }else{
//                Log.e(TAG, "getDetailStory: error not succes ${response.code()}")
//                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(response.code())
//                Resource.Error(errorMsg)
//            }
//        }catch (e: IOException){
//            Resource.Error("Errror koneksi")
//        }catch (e: Exception){
//            Log.e(TAG, "getDetailStory: error", e)
//            Resource.Error("Errrorr")
//        }
//    }

    suspend fun uploadStory(imageFile: File, desc: String): Resource<String> {
        Resource.Loading<String>()
        return try {
            val requestBody = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.uploadStory(multipartBody, requestBody)
            Log.d(TAG, "uploadStory: respons $response")
            if (response.isSuccessful) {
                val story = response.body()
                if (story?.error == false) {
                    Resource.Success(story.message.toString())
                } else {
                    Resource.Empty()
                }
            }else{
                val errorMsg = ApiUtils.parseError(response.errorBody())?.message ?: errorHandling(response.code())
                Log.e(TAG, "uploadStory: error not sukses $errorMsg")
                Resource.Error(errorMsg)
            }
        }catch (e: Exception) {
            Log.e(TAG, "uploadStory: error Exception", e)
            Resource.Error(e.toString())
        }
    }

    // language
    suspend fun getCurrentLanguage(): String {
        return userPref.getLang().first()
    }

    suspend fun setLanguage(langCode: String){
        userPref.saveLangCode(langCode)
    }

    // widget
    fun getItemWidget(): List<ListStoryItem>{
        return try {
            val response = apiService.getWidgetItems().execute() // ✅ Response<StoryResponse>
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("StackWidget", "API success, items: ${body?.listStory?.size}")
                body?.listStory ?: emptyList()
            } else {
                Log.e("StackWidget", "API error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("StackWidget", "API exception: ${e.message}")
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