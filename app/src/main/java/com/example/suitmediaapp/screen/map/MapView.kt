package com.example.suitmediaapp.screen.map

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.suitmediaapp.R
import com.example.suitmediaapp.databinding.ActivityMapViewBinding
import com.example.suitmediaapp.shared.ext.getStringRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapView : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapViewBinding
    private lateinit var map: GoogleMap
    private var longitude: String? = null
    private var latitude: String? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MapView::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        with(binding) {
            customToolbar.apply {
                setTitle(getStringRes(R.string.maps_title))
                setSupportActionBar(toolbar())
                supportActionBar?.title = title
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(false)
                toolbar().navigationIcon = resources.getDrawable(R.drawable.ic_arrow_left)
                toolbar().setNavigationOnClickListener { onBackPressed() }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        longitude = "151.0"
        latitude = "-34.0"
        if (longitude == null && latitude == null) {
            val marker = LatLng(latitude!!.toDouble(), longitude!!.toDouble())
            marker.let { MarkerOptions().position(it) }.let { map.addMarker(it) }
            map.moveCamera(CameraUpdateFactory.newLatLng(marker))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker,16.0f))
        } else {
            val marker = LatLng(latitude!!.toDouble(), longitude!!.toDouble())
            marker.let { MarkerOptions().position(it) }.let { map.addMarker(it) }
            map.moveCamera(CameraUpdateFactory.newLatLng(marker))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker,16.0f))
        }
    }
}