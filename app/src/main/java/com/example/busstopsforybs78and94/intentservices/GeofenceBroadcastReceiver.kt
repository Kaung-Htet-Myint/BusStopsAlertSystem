package com.example.busstopsforybs78and94.intentservices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GeofenceTransitionsJobIntentService.enqueueWork(context,intent)
    }
}