package com.example.busstopsforybs78and94

import android.provider.ContactsContract.Directory.PACKAGE_NAME
import com.google.android.gms.maps.model.LatLng
import java.util.HashMap

internal object Constants {

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12
    val GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY"
    /**
     * For this sample, geofences expire after twelve hours.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS =
        GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    val GEOFENCE_RADIUS_IN_METERS = 1609f // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    val BAY_AREA_LANDMARKS = HashMap<String, LatLng>()


}