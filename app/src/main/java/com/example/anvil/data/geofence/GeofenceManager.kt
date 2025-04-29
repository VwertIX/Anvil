package com.example.anvil.data.geofence


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await


// code from https://github.com/android/platform-samples/tree/main/samples/location/src/main/java/com/example/platform/location/geofencing referenced in making the geofencing support
// and https://developer.android.com/develop/sensors-and-location/location/geofencing
class GeofenceManager(context: Context) {

    private val CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION"
    private val CUSTOM_REQUEST_CODE_GEOFENCE = 1001


    private val client = LocationServices.getGeofencingClient(context)
    val geofenceList = mutableMapOf<String, Geofence>()

    private val geofencingPendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            CUSTOM_REQUEST_CODE_GEOFENCE,
            Intent(CUSTOM_INTENT_GEOFENCE),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun addGeofence(
        id: String,
        location: Location,
        radiusInMeters: Float = 100.0f,
        transitionType: Int = GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT
    ) {
        geofenceList[id] = createGeofence(id, location, radiusInMeters, transitionType)
    }

    fun removeGeofence(key: String) {
        geofenceList.remove(key)
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence() {
        client.addGeofences(createGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceManager", "registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Log.d("GeofenceManager", "registerGeofence: Failure\n$exception")
            }
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
        geofenceList.clear()
        Log.d("GeofenceManager", "DeregisterGeofence: SUCCESS")
    }

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GEOFENCE_TRANSITION_ENTER)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

    private fun createGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float,
        transitionType: Int
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setTransitionTypes(transitionType)
            .build()
    }

}