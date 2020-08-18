package com.example.busstopsforybs78and94.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.busstopsforybs78and94.R
import com.example.busstopsforybs78and94.intentservices.GeofenceTransitionsIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

class GeofenceUtils(private val context: Context) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)

    companion object {

        fun getErrorString(context: Context, errorCode: Int): String {
            val resources = context.resources
            when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return resources.getString(R.string.geofence_not_available)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return resources.getString(R.string.geofence_registered_too_many)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return resources.getString(R.string.geofence_provided_too_many_pending_intent)
                else -> return resources.getString(R.string.geofence_unknown_error)
            }
        }

        fun getGeofenceTransitionDetails(geofenceList: List<Geofence>): String {
            val geofenceDetails = StringBuilder()

            for (geofence in geofenceList) {
                geofenceDetails.append(geofence.requestId + ", ")
            }

            return geofenceDetails.toString()
        }

        fun getGeofenceTransition(context: Context, geofenceTransition: Int): String {
            val resources = context.resources
            when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> return resources.getString(R.string.geofence_transition_enter)
                Geofence.GEOFENCE_TRANSITION_EXIT -> return resources.getString(R.string.geofence_transition_exit)
                else -> return resources.getString(R.string.geofence_transition_unknown)
            }
        }

    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitionsIntentService::class.java)
        PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildGeofence(latitude: Double, longitude: Double): Geofence? {

        val radius = 100

        if (latitude != null && longitude != null && radius != null) {
            return Geofence.Builder()
                .setRequestId(UUID.randomUUID().toString())
                .setCircularRegion(
                    latitude,
                    longitude,
                    radius.toFloat()
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        return null
    }

    private fun buildGeofencingRequest(geofenceList: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(0)
            .addGeofences(geofenceList)
            .build()
    }

    fun addTestGeofence(latALngList: List<LatLng>) {

        val geofenceList = mutableListOf<Geofence>()

        for (i in 0 until latALngList.size) {
            val geofence =
                buildGeofence(latALngList[i].latitude, latALngList[i].longitude)
            geofenceList.add(i, geofence!!)

        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient
                .addGeofences(buildGeofencingRequest(geofenceList), geofencePendingIntent)
                .addOnSuccessListener {
                    Log.e("GEOF_Status", "Success")
                    Toast.makeText(context, "Geo Points Registration Success", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Log.e("GEOF_Status", "$it")
                    Toast.makeText(context, "Geo Points Registration Fail", Toast.LENGTH_LONG).show()
                }
        }
    }
}