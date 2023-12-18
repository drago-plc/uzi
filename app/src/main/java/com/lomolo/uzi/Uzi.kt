package com.lomolo.uzi

import android.app.Application

class UziApp: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultContainer()
    }
}