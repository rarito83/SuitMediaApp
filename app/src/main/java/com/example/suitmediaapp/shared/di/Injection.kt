package com.example.suitmediaapp.shared.di

import com.example.suitmediaapp.data.RemoteDataSource.Companion.getRemoteDataSource
import com.example.suitmediaapp.repository.UserRepository
import com.example.suitmediaapp.repository.UserRepository.Companion.getRepository

object Injection {

    fun provideRepository(): UserRepository {
        getRemoteDataSource().also {
            return getRepository(it)
        }
    }

}