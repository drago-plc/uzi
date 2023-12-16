package com.lomolo.uzi.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Priority

object LocationPermission {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun hasLocationPermission(
        hasPreciseLocationPermission: Boolean,
        hasApproximateLocationPermission: Boolean
    ): Boolean {
        return hasPreciseLocationPermission || hasApproximateLocationPermission
    }
    fun checkSelfLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun decideLocationPermissionState(
        hasPermissionLocation: Boolean,
        shouldShowPermissionRationale: Boolean
    ): String {
        return if (hasPermissionLocation) "Granted"
        else if (shouldShowPermissionRationale) "Rejected"
        else "Denied"
    }
    fun openApplicationSettings(context: Context) {
        val openSetting: Intent = Intent(
            Settings.ACTION_APPLICATION_SETTINGS,
            Uri.fromParts("package", "com.lomolo.uzi", null))
            .also {
                context.startActivity(it)
            }
    }
    fun locationPriority(
        usePreciseLocation: Boolean,
        useApproximateLocation: Boolean
    ): Int {
        var priority: Int? = null
        return when {
            usePreciseLocation -> {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                priority
            }

            useApproximateLocation -> {
                priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
                priority
            }

            else -> {
                priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
                priority
            }
        }
    }
}