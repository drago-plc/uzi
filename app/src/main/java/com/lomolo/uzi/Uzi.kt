package com.lomolo.uzi

import android.app.Application

class UziApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DefaultContainer()
    }
}