package com.example.storyapp

import android.content.Context
import android.util.Log
import com.example.storyapp.datasource.db.UserPreferences
import com.example.storyapp.remote.retrofit.ApiConfig
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val token = userPreferences.getToken()
        val apiService = ApiConfig.getApiService(token)
        return UserRepository.getInstance(apiService, userPreferences)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val token = userPreferences.getToken()
        Log.d("Tokenflow", token.toString())
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService)
    }
}