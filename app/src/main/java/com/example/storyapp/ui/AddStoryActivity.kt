package com.example.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.ui.viewmodel.AddStoryViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        binding.switchShareLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                getMyLastLocation()
            }
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.button5.setOnClickListener { lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                uploadStory()
            }
        }}
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imgStory.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {

                    getMyLastLocation()
                }
                else -> {}
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude

                    Toast.makeText(
                        this@AddStoryActivity,
                        "Latitude: $lat, Longitude: $lon",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
   private suspend fun uploadStory() {
       val description = binding.mlDescription.text.toString()
       val latString = lat.toString()
       val lonString = lon.toString()

       if (currentImageUri != null && description.isNotEmpty()) {

           val file = uriToFile(this, currentImageUri!!)
           val compressFile = compressFile(this, file)

           val requestDescription = createRequestBodyFromText(description)
           val requestImageFile = createRequestBodyFromFile(compressFile)
           val requestLat = createRequestBodyFromText(latString)
           val requestLon = createRequestBodyFromText(lonString)

           val uploadStory = {
               if (binding.switchShareLocation.isChecked){
                   viewModel.uploadStoryWithLocation(requestImageFile, requestDescription, requestLat, requestLon)

               } else {
                   viewModel.uploadStory(requestImageFile, requestDescription)
               }
           }

           uploadStory().observe(this){ result ->
               when (result) {
                   is ResultState.Loading -> {
                       isLoading(binding.button5, binding.pbButton)
                   }
                   is ResultState.Success -> {
                       isNotLoading(binding.button5, binding.pbButton)
                       val intent = Intent(this, MainActivity::class.java)
                       intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                       startActivity(intent)
                   }
                   is ResultState.Error -> {
                       isNotLoading(binding.button5, binding.pbButton)
                       Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                   }
               }
           }
       } else {
           Toast.makeText(this, "Deskripsi dan gambar harus diisi", Toast.LENGTH_SHORT).show()
       }
   }


}