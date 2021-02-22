package com.example.zomato.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@SuppressLint("MissingPermission")
class LocationHelper(
    context: Context
) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var onLocationChanged: ((Location) -> Unit)? = null
    private val locationListenerGps: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onLocationChanged?.invoke(location)
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    fun attachObserver(onLocationChanged: (Location) -> Unit) {
        this.onLocationChanged = onLocationChanged
    }

    fun stopListening() {
        locationManager.removeUpdates(locationListenerGps)
    }

    fun startListening() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            250f,
            locationListenerGps
        )
    }

    fun isConnected(): LiveData<Boolean> {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return MutableLiveData(
                locationManager.isLocationEnabled && locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
            )
        } else
            return MutableLiveData(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

}