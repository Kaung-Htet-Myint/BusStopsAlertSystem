package com.example.busstopsforybs78and94.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.busstopsforybs78and94.BuildConfig
import com.example.busstopsforybs78and94.R

const val BA_ACTIVITIES_DETECTED = "${ContactsContract.Directory.PACKAGE_NAME}.BA_ACTIVITIES_DETECTED"
const val IE_DETECTED_ACTIVITIES = "${ContactsContract.Directory.PACKAGE_NAME}.IE_DETECTED_ACTIVITIES"
const val ACTIVITY_DETECTION_INTERVAL = 1000

fun locationPermissionsAreGranted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context!!,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}

fun buildAndSendNotification(context: Context, title: String, message: String, pendingIntent: PendingIntent) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }

    val notification = buildNoti(context, NOTIFICATION_CHANNEL_ID, title, message, pendingIntent)

    notificationManager.notify(getUniqueId(), notification)
}


private fun buildNoti(context: Context, channelId: String, title: String, content: String, intent: PendingIntent): Notification {
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    return NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(content)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(content)
        )
        .setContentIntent(intent)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .build()
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())