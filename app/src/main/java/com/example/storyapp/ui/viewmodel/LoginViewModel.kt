package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.UserRepository

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
}