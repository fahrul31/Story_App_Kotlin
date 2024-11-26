package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository

class MapsViewModel (private val repository: StoryRepository): ViewModel() {
    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}