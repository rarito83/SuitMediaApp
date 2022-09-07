package com.example.suitmediaapp.data

import android.util.Log
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.data.remote.UserResponse
import com.example.suitmediaapp.network.ApiNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource {

    companion object {
        @Volatile
        private var remoteDataSource: RemoteDataSource? = null

        fun getRemoteDataSource(): RemoteDataSource {
            return remoteDataSource ?: synchronized(this) {
                RemoteDataSource().apply {
                    remoteDataSource = this
                }
            }
        }
    }

    fun getUsers(callback: LoadUsersCallback) {
        ApiNetwork.apiService.getUser().enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                callback.onAllUsersReceived(response.body()?.data)
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Log.e("Remote Data Source", "Data User Failed ${t.message}")
            }
        })
    }

    interface LoadUsersCallback {
        fun onAllUsersReceived(userList: List<User>?)
    }

}