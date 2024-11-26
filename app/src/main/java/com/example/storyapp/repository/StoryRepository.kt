package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.remote.response.DefaultResponse
import com.example.storyapp.remote.response.Story
import com.example.storyapp.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {

    fun getStories(): LiveData<PagingData<Story>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<ResultState<List<Story>>>{
        return liveData{
            emit(ResultState.Loading)
            try {
                val response = apiService.getStoriesWithLocation()
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        if (responseBody.error) {
                            emit(ResultState.Error(responseBody.message ))
                        } else {
                            emit(ResultState.Success(responseBody.listStory))
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

    fun getDetailStory(id: String):LiveData<ResultState<Story>> {
        return liveData{
            emit(ResultState.Loading)
            try {
                val response = apiService.getDetailStory(id)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        if (responseBody.error) {
                            emit(ResultState.Error(responseBody.message))
                        } else {
                            emit(ResultState.Success(responseBody.story!!))
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

    fun uploadStory(file: MultipartBody.Part, description: RequestBody):LiveData<ResultState<DefaultResponse>> {
        return liveData{
            emit(ResultState.Loading)
            try {
                Log.d("uploadStory", "uploadStory: $file, $description")


                val response = apiService.uploadStory(file, description)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        if (responseBody.error == true) {
                            emit(ResultState.Error(responseBody.message ?: "Unknown error"))
                        } else {
//                            Log.d("storyMessage", responseBody.listStory.toString())
                            emit(ResultState.Success(responseBody))
                        }
                    } else {
                        emit(ResultState.Error("Response body is null"))
                    }
                } else {
                    val errorMessage = try {
                        val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                        val errorResponse = Gson().fromJson(errorBodyString, DefaultResponse::class.java)
                        errorResponse?.message ?: errorBodyString
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

    fun uploadStoryWithLocation(file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody):LiveData<ResultState<DefaultResponse>> {
        return liveData{
            emit(ResultState.Loading)
            try {
                Log.d("uploadStory", "uploadStory: $file, $description")

                val response = apiService.uploadStory(file, description, lat, lon)
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
                        val errorResponse = Gson().fromJson(errorBodyString, DefaultResponse::class.java)
                        errorResponse?.message ?: errorBodyString
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
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}