package com.example.suitmediaapp.data

import androidx.lifecycle.LiveData
import com.example.suitmediaapp.data.model.User

interface DataSource {
    fun loadDataUser(): LiveData<List<User>>
}