package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.ui.viewmodel.SignUpViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.myToolbar)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        binding.buttonSign.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Oops! Email dan Password wajib diisi untuk melanjutkan.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(name, email, password).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            isLoading(binding.buttonSign, binding.pbButtonSign)
                        }
                        is ResultState.Success -> {
                            isNotLoading(binding.buttonSign, binding.pbButtonSign)
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finishAffinity()
                        }
                        is ResultState.Error -> {
                            isNotLoading(binding.buttonSign, binding.pbButtonSign)
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
}