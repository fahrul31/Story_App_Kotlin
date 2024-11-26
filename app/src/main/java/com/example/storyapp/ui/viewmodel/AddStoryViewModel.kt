package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun uploadStory(file: MultipartBody.Part, description: RequestBody) = storyRepository.uploadStory(file, description)
    fun uploadStoryWithLocation(file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody) = storyRepository.uploadStoryWithLocation(file, description, lat, lon)
}