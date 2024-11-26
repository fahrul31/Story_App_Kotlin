package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.remote.response.Story
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository

class MainViewModel(private val repository: StoryRepository, private val userRepository: UserRepository): ViewModel() {
    fun getStories(): LiveData<PagingData<Story>> = repository.getStories().cachedIn(viewModelScope)

    suspend fun logout() : Boolean = userRepository.logout()
}