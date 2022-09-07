package com.example.suitmediaapp.screen.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.repository.UserRepository

class GuestViewModel(private val repository: UserRepository) : ViewModel() {

    fun getUsers(): LiveData<List<User>> = repository.loadDataUser()
}