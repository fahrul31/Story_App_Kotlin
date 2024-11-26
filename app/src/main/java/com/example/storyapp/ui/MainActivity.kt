package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.MainAdapter
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: MainAdapter
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        setStoryData()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }



        binding.fabLogout.setOnClickListener {
            lifecycleScope.launch {
                val menuItem = binding.topAppBar.menu.findItem(R.id.action_maps)
                menuItem.isVisible = false
                isLoadingRv(binding.rvStories, binding.progressContainer, binding.fabAddStory, binding.fabLogout)
                val isLogOut = viewModel.logout()
                delay(3000)
                if (isLogOut) {
                    val intent = Intent(this@MainActivity, LandingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_maps -> {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setStoryData() {
        storyAdapter = MainAdapter()

        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        viewModel.getStories().observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }
}