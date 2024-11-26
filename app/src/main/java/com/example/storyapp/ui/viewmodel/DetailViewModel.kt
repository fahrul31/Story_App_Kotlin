package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository

class DetailViewModel(private val id: String, private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getDetailStory(id)
}