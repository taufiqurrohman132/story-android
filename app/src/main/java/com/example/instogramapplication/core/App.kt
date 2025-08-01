package com.example.instogramapplication.core

import android.app.Application
import android.content.Context
import com.yariksoffice.lingver.Lingver

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", "id") ?: "id"

        Lingver.init(this, langCode)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}