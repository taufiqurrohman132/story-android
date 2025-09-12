package com.example.instogramapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner

class CustomTestRunner : AndroidJUnitRunner() {
    override fun onStart() {
        val packageName = targetContext.packageName
        val permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
        permissions.forEach {
            uiAutomation.executeShellCommand("pm grant $packageName $it").close()
        }
        super.onStart()
    }
}

