package com.example.suitmediaapp.data.remote

import com.example.suitmediaapp.data.model.User
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("data")
    val data: List<User>

)