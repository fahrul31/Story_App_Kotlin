package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.UserRepository

class SignUpViewModel(private val repository: UserRepository): ViewModel() {

    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}