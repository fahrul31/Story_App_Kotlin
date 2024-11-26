package com.example.storyapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Injection
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository

class ViewModelFactory(private val id: String? = null, private val userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory()  {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("ViewModelFactory", "Creating ViewModel with ID: $id")

        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, userRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            if (id != null) {
                return DetailViewModel(id, storyRepository) as T
            } else {
                throw IllegalArgumentException("ID is required for DetailViewModel")
            }
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository) as T
        } else if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, id: String? = null): ViewModelFactory {
            val userRepository = Injection.provideUserRepository(context)
            val storyRepository = Injection.provideStoryRepository(context)
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(id, userRepository, storyRepository)
                    .also { INSTANCE = it }
            }
        }

        @JvmStatic
        fun newInstance(context: Context, id: String?): ViewModelFactory {
            val userRepository = Injection.provideUserRepository(context)
            val storyRepository = Injection.provideStoryRepository(context)
            return ViewModelFactory(id, userRepository, storyRepository)
        }

    }
}