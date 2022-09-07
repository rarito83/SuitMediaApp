package com.example.suitmediaapp.network

import com.example.suitmediaapp.data.remote.UserResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("users?page=1&per_page=10")
    fun getUser(): Call<UserResponse>

}