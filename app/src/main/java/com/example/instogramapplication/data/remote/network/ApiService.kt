package com.example.instogramapplication.data.remote.network

import com.example.instogramapplication.data.remote.model.FileUploadResponse
import com.example.instogramapplication.data.remote.model.LoginResponse
import com.example.instogramapplication.data.remote.model.RegisterResponse
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.data.remote.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name")
        name: String,
        @Field("email")
        email: String,
        @Field("password")
        password: String
    ): Response<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email")
        email: String,
        @Field("password")
        password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<StoryResponse>

    @GET("stories")
    fun getWidgetItems(): Call<StoryResponse>

    @POST("stories")
    @Multipart
    suspend fun uploadStory(
        @Part
        file: MultipartBody.Part,
        @Part("description")
        description: RequestBody
    ): Response<FileUploadResponse>

}