package com.example.busstopsforybs78and94.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.busstopsforybs78and94.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment: Fragment() {
    companion object {
        const val TAG = "MapFragment"
        fun newInstance(): Fragment {
            return MapFragment()
        }
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        context?.let {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(it)
        }

        setUpMap()

        return view
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            mMap = it
            mMap.uiSettings.isZoomControlsEnabled = true

            val padc = LatLng(16.779778, 96.170582)

            putMarkerOnLocation(padc.latitude, padc.longitude, "Here I'm")
            focusTotLocation(padc.latitude, padc.longitude)
        }

    }

    private fun focusTotLocation(latitude: Double, longitude: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    latitude, longitude
                )
            )      // Sets the center of the map to location user
            .zoom(20f)                   // Sets the zoom
            .bearing(90f)                // Sets the orientation of the camera to east
            .tilt(40f)                   // Sets the tilt of the camera to 30 degrees
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

//        val latLng = LatLng(latitude, longitude)
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f))
    }

    private fun putMarkerOnLocation(latitude: Double, longitude: Double, name: String) {
        val location = LatLng(latitude, longitude)
        if (::mMap.isInitialized) mMap.addMarker(MarkerOptions().position(location).title(name))
    }
}