package com.lomolo.uzi

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/*
 * Uzi app view model factory provider
 */
object UziViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainViewModel(uziApplication().container.uziRestApiService)
        }
    }
}

/*
 * Grab instance of uzi application
 */
fun CreationExtras.uziApplication(): UziApp = (this[AndroidViewModelFactory.APPLICATION_KEY] as UziApp)