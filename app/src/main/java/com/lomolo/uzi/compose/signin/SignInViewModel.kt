package com.lomolo.uzi.compose.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.model.SignIn
import com.lomolo.uzi.network.UziRestApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

class SignInViewModel(
    private val uziRestApiService: UziRestApiService,
    mainViewModel: MainViewModel
): ViewModel() {
    private val _signInInput = MutableStateFlow(SignIn())
    val signInInput: StateFlow<SignIn> = _signInInput.asStateFlow()

    var signInUiState: SignInUiState by mutableStateOf(SignInUiState.Success)
        private set

    private val phoneUtil = PhoneNumberUtil.getInstance()
    private val deviceDetails = mainViewModel.deviceDetailsUiState

    fun setFirstname(name: String) {
        _signInInput.update {
            it.copy(firstName = name)
        }
    }
    fun setLastname(name: String) {
        _signInInput.update {
            it.copy(lastName = name)
        }
    }
    fun setPhone(phone: String) {
        _signInInput.update {
            it.copy(phone = phone)
        }
    }

    private fun validName(name: String): Boolean {
        return name.contains(Regex("[^A-Za-z ]"))
    }
    fun validFirstname(uiState: SignIn): Boolean {
        return with(uiState) {
            validName(firstName)
        }
    }
    fun validLastname(uiState: SignIn): Boolean {
        return with(uiState) {
            validName(lastName)
        }
    }

    fun validPhone(uiState: SignIn): Boolean {
        return with(uiState) {
            validatePhonenumber(phone)
        }
    }
    private fun validatePhonenumber(number: String): Boolean {
        return try {
            if (number.isEmpty()) return true
            val p = Phonenumber.PhoneNumber()
            p.countryCode = deviceDetails.value.countryPhoneCode.toInt()
            p.nationalNumber = number.toLong()
            return phoneUtil.isValidNumber(p)
        } catch(e: Exception) {
            false
        }
    }

    fun signIn(cb: () -> Unit = {}) {
        signInUiState = SignInUiState.Loading
        viewModelScope.launch {
            signInUiState = try {
                uziRestApiService.signIn(signInInput.value)
                SignInUiState.Success.also { cb() }
            } catch(e: Exception) {
                SignInUiState.Error(e.localizedMessage)
            }
        }
    }
}

interface SignInUiState {
    data object Loading: SignInUiState
    data object Success: SignInUiState
    data class Error(val message: String?): SignInUiState
}