package com.example.suitmediaapp.shared.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.suitmediaapp.repository.UserRepository
import com.example.suitmediaapp.screen.guest.GuestViewModel
import com.example.suitmediaapp.shared.di.Injection

class ViewModelFactory private constructor(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository()).apply {
                    instance = this
                }
            }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GuestViewModel::class.java) -> {
                GuestViewModel(repository) as T
            }

            else -> throw Throwable("Unknown ViewModel: " + modelClass.name)
        }
    }

}