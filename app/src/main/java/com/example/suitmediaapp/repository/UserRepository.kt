package com.example.suitmediaapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suitmediaapp.data.DataSource
import com.example.suitmediaapp.data.RemoteDataSource
import com.example.suitmediaapp.data.model.User

class UserRepository private constructor(private val remoteDataSource: RemoteDataSource):

    DataSource {

    companion object {
        @Volatile
        private var repository: UserRepository? = null

        fun getRepository(remoteDataSource: RemoteDataSource): UserRepository {
            return repository ?: synchronized(this) {
                UserRepository(remoteDataSource).apply {
                    repository = this
                }
            }
        }
    }


    override fun loadDataUser(): LiveData<List<User>> {
        val getUsers = MutableLiveData<List<User>>()
        remoteDataSource.getUsers(object : RemoteDataSource.LoadUsersCallback {
            override fun onAllUsersReceived(userList: List<User>?) {
                val usersEntity = ArrayList<User>()
                if (userList != null) {
                    for (users in userList) {
                        users.apply {
                            val usersEntities = User(
                                id,
                                email,
                                first_name,
                                last_name,
                                avatar
                            )
                            usersEntity.add(usersEntities)
                        }
                    }
                    getUsers.postValue(usersEntity)
                }
            }
        })
        return getUsers
    }
}