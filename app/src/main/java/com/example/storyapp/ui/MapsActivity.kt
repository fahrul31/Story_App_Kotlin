package com.example.storyapp.ui

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.datasource.ResultState
import com.example.storyapp.remote.response.Story
import com.example.storyapp.ui.viewmodel.MapsViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {

                is ResultState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Success -> {
                    Log.d("MapsActivityData", "onCreate: ${result.data}")
                    result.data.forEach { data ->
                        val latLng = LatLng(data.lat!!, data.lon!!)
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title(data.name)
                            .snippet(data.description)

                        val marker = mMap.addMarker(markerOptions)
                        marker?.tag = data
                        boundsBuilder.include(latLng)
                    }

                }
                is ResultState.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        val indonesia = LatLng(-6.20, 106.81)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))

        mMap.setOnMarkerClickListener { marker ->

            val storyData = marker.tag as? Story
            if (storyData != null) {
                binding.nameText.text = storyData.name
                binding.descriptionText.text = storyData.description


                binding.titleDescriptionContainer.visibility = View.VISIBLE
            }
            true
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Style parsing failed.", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Resources.NotFoundException) {
        }
    }
}