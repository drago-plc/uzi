package com.lomolo.uzi

import android.app.Application
import com.lomolo.uzi.container.AppContainer
import com.lomolo.uzi.container.DefaultContainer

class UziApp: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultContainer(this)
    }
}