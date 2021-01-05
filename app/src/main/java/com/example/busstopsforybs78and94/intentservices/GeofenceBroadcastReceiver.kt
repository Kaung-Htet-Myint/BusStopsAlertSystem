package com.example.busstopsforybs78and94.intentservices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GeofenceTransitionsJobIntentService.enqueueWork(context,intent)
        Toast.makeText(context,"Testing",Toast.LENGTH_SHORT).show()
    }
}