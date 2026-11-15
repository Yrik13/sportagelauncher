package com.mylauncher

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import android.os.Bundle

class GpsSpeed(
    private val context: Context,
    private val onSpeed: (Int) -> Unit
) : LocationListener {

    fun start() {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 0f, this)
            }
        } catch (_: Exception) {}
    }

    override fun onLocationChanged(location: Location) {
        // скорость в м/с → км/ч
        onSpeed((location.speed * 3.6).toInt())
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}
