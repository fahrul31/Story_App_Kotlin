package com.example.storyapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.remote.response.Story
import com.example.storyapp.ui.viewmodel.DetailViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailViewModel

    companion object {
        const val ID_STORY = "id_story"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        val storyId = intent.getStringExtra(ID_STORY)

        val viewModelFactory = ViewModelFactory.newInstance(this, storyId)
        viewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]
        viewModel.getStories().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Success -> {
                    setStoryData(result.data)
                }
                is ResultState.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setStoryData(story: Story){
        binding.username.text = story.name
        binding.tvDescription.text = story.description
        Glide.with(this)
            .load(story.photoUrl).fitCenter()
            .into(binding.imgStory)
    }


}