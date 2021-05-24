package com.example.izzy.models

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LocationManager {

    private const val TAG = "LocationManager"

    private var locationRequest : LocationRequest? = null

    private var fusedLocationClient : FusedLocationProviderClient? = null

    data class LocationSettingsResult(val success: Boolean? = false, val ex: Exception? = null)

    private val locationLiveData = MutableLiveData<LocationData>()

    val location: LiveData<LocationData> = locationLiveData

    suspend fun requestLocationServices(context: Context) : LocationSettingsResult {

        return suspendCoroutine { continuation ->

            createLocationRequest()

            val builder = locationRequest?.let { LocationSettingsRequest.Builder().addLocationRequest(it) }
            val task = LocationServices.getSettingsClient(context).checkLocationSettings(builder?.build())

            task.addOnSuccessListener { response ->
                if(response.locationSettingsStates.isNetworkLocationPresent)
                    continuation.resume(LocationSettingsResult(true))
            }.addOnFailureListener { exception ->
                if(exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(context as Activity, Config.REQUEST_CHECK_SETTINGS_CODE)
                    } catch (ex : IntentSender.SendIntentException) {
                        continuation.resume(LocationSettingsResult(ex = exception))
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {

        createLocationRequest()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Config.UPDATE_INTERVAL
            fastestInterval = Config.FASTEST_INTERVAL
            this.priority = Config.LOCATION_PRIORITY
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(response: LocationResult?) {
            response?: return
            for(location in response.locations)
                setLocationData(location)

            super.onLocationResult(response)
        }
    }

    private fun setLocationData(location: Location) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.UK)
        this.locationLiveData.value = LocationData(
                longitude = location.longitude,
                latitude = location.latitude,
                speed = location.speed,
                timestamp = sdf.format(location.time),
                accuracy = location.accuracy,
                altitude = location.altitude,
                bearing = location.bearing,
        )
    }

    fun checkLocationEnabled(context: Context) : Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        if(!manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
            return false
        return true
    }
}