package com.example.storyapp.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.datasource.db.UserPreferences
import com.example.storyapp.remote.response.DefaultResponse
import com.example.storyapp.remote.response.LoginResponse
import com.example.storyapp.remote.response.RegisterResponse
import com.example.storyapp.remote.retrofit.ApiService
import com.google.gson.Gson

class UserRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {
    fun register(name: String, email: String, password: String): LiveData<ResultState<RegisterResponse>> {
        return liveData{
            emit(ResultState.Loading)
            try {
                val response = apiService.register(name, email, password)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.error == true) {
                            emit(ResultState.Error(responseBody.message ?: "Unknown error"))
                        } else {
                            emit(ResultState.Success(responseBody))
                        }
                    } else {
                        emit(ResultState.Error("Response body is null"))
                    }
                } else {
                    val errorMessage = try {
                        val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                        val defaultResponse = Gson().fromJson(errorBodyString, DefaultResponse::class.java)
                        defaultResponse?.message ?: errorBodyString
                    } catch (e: Exception) {
                        "Error parsing errorBody"
                    }
                    emit(ResultState.Error(errorMessage))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "Exception occurred"))
            }
        }
    }

    suspend fun logout() : Boolean {
        return userPreferences.clear()
    }

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> {
        return liveData{
            emit(ResultState.Loading)
            try {
                val response = apiService.login(email, password)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.error) {
                            emit(ResultState.Error(responseBody.message))
                        } else {
                            emit(ResultState.Success(responseBody))
                            responseBody.loginResult?.token!!.let {
                                userPreferences.saveToken(it)
                                Log.d("Token", it)
                            }
                        }
                    }
                } else {
                    val errorMessage = try {
                        val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                        val defaultResponse = Gson().fromJson(errorBodyString, DefaultResponse::class.java)
                        defaultResponse?.message ?: errorBodyString
                    } catch (e: Exception) {
                        "Error parsing errorBody"
                    }
                    emit(ResultState.Error(errorMessage))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "Exception occurred"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreferences)
        }.also { instance = it }
    }
}