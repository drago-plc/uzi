package com.lomolo.uzi

import android.app.Application

class UziApp: Application() {
    val container = DefaultContainer()
    override fun onCreate() {
        super.onCreate()
    }
}