package com.godzuche.outdoorexplorer.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.data.OutdoorRoomDatabase
import com.godzuche.outdoorexplorer.data.OutdoorRoomRepository
import com.godzuche.outdoorexplorer.ui.location.LocationFragmentArgs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)

        if (event.hasError())
            return

        //confirm that the transition type is what we are expecting -enter
        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            //the event returns an array so let's take the first cause multiple fired geofence could overlap
            val geofence = event.triggeringGeofences[0]
            sendNotification(context, geofence.requestId.toInt())
        }
    }

    private fun sendNotification(context: Context, locationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //notification channel for android O or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("locations",
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }

        //setting navigation instructions to pass the id from geofence
        val locationArgs = LocationFragmentArgs.Builder()
            .setLocationId(locationId).build().toBundle()
        val intent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_location)
            .setArguments(locationArgs)
            .createPendingIntent()

        //a message for the specific location the user is in...
        val outdoorDao = OutdoorRoomDatabase.getInstance(context).outdoorDao()
        val outdoorRepository = OutdoorRoomRepository(outdoorDao)
        val location = outdoorRepository.getLocationById(locationId)
        val message = context.getString(R.string.notification_message, location.title)
        val notification = NotificationCompat.Builder(context, "location")
            .setSmallIcon(R.drawable.ic_star_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(message)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)

    }

}
