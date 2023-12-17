package com.lomolo.uzi.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object LocationPermission {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
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
}