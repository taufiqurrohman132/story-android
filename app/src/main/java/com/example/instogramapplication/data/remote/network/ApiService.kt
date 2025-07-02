package com.example.instogramapplication.data.remote.network

import com.example.instogramapplication.data.remote.model.LoginResponse
import com.example.instogramapplication.data.remote.model.RegisterResponse
import com.example.instogramapplication.data.remote.model.StoryResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST

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
    suspend fun getStories(): Response<StoryResponse>


}