package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.ui.viewmodel.LoginViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        playAnimation()

        binding.button.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Oops! Email dan Password wajib diisi untuk melanjutkan.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.login(email, password).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            isLoading(binding.button, binding.pbButtonLogin)
                        }

                        is ResultState.Success -> {
                            isNotLoading(binding.button, binding.pbButtonLogin)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }

                        is ResultState.Error -> {
                            isNotLoading(binding.button, binding.pbButtonLogin)
                            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(150)
        val description = ObjectAnimator.ofFloat(binding.tvDescriptionLogin, View.ALPHA, 1f).setDuration(150)
        val login = ObjectAnimator.ofFloat(binding.editTextEmail, View.ALPHA, 1f).setDuration(150)
        val textLogin = ObjectAnimator.ofFloat(binding.textInputLayout, View.ALPHA, 1f).setDuration(150)
        val pass = ObjectAnimator.ofFloat(binding.editTextPassword, View.ALPHA, 1f).setDuration(150)
        val textPass = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(150)
        val button = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(title, description,  textLogin, login, textPass, pass, button)
            start()
        }
    }
}