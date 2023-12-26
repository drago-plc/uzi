package com.lomolo.uzi

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lomolo.uzi.compose.signin.SignInViewModel

/*
 * Uzi app view model factory provider
 */
object UziViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel

        initializer {
            mainViewModel = MainViewModel(uziApplication().container.uziRestApiService)
            mainViewModel
        }
        initializer {
            SignInViewModel(
                uziApplication().container.sessionRepository,
                mainViewModel
            )
        }
    }
}

/*
 * Grab instance of uzi application
 */
fun CreationExtras.uziApplication(): UziApp = (this[AndroidViewModelFactory.APPLICATION_KEY] as UziApp)