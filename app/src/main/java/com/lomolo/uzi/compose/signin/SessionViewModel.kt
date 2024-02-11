package com.lomolo.uzi.compose.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.model.Session
import com.lomolo.uzi.model.SignIn
import com.lomolo.uzi.repository.SessionInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

class SessionViewModel(
    private val sessionRepository: SessionInterface,
    mainViewModel: MainViewModel
): ViewModel() {
    val sessionUiState: StateFlow<Session> = sessionRepository
        .getSession()
        .filterNotNull()
        .map {
            if (it.isNotEmpty()) {
                Session(
                    token = it[0].token,
                    id = it[0].id,
                    firstname = it[0].firstname,
                    lastname = it[0].lastname,
                    courierStatus = it[0].courierStatus,
                    phone = it[0].phone,
                    isCourier = it[0].isCourier,
                    onboarding = it[0].onboarding
                )
            } else {
                Session()
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = Session(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

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

    fun isNameValid(name: String): Boolean {
        return name.trim().isNotBlank() && name.trim().matches(Regex("^[a-zA-Z ]*$"))
    }

    fun isPhoneValid(uiState: SignIn): Boolean {
        return with(uiState) {
            isPhoneNumberValid(phone)
        }
    }

    private fun isPhoneNumberValid(number: String): Boolean {
        return try {
            if (number.isEmpty()) return false
            val p = Phonenumber.PhoneNumber()
            p.countryCode = deviceDetails.value.countryPhoneCode.toInt()
            p.nationalNumber = number.toLong()
            return phoneUtil.isValidNumber(p)
        } catch(e: Exception) {
            false
        }
    }

    private fun parsePhoneNumber(number: String): String {
        val p = phoneUtil.parse(number, deviceDetails.value.country)
        return p.countryCode.toString()+p.nationalNumber.toString()
    }

    fun signIn(cb: () -> Unit = {}) {
        signInUiState = SignInUiState.Loading
        viewModelScope.launch {
            signInUiState = try {
                sessionRepository.signIn(
                    SignIn(
                        phone = parsePhoneNumber(signInInput.value.phone),
                        courier = signInInput.value.courier
                    )
                )
                SignInUiState.Success.also { cb() }
            } catch(e: Exception) {
                SignInUiState.Error(e.localizedMessage)
            }
        }
    }

    fun onboardUser(cb: () -> Unit = {}) {
        signInUiState = SignInUiState.Loading
        viewModelScope.launch {
            signInUiState = try {
                sessionRepository.onboardUser(
                    sessionUiState.value.id,
                    SignIn(
                        firstName = signInInput.value.firstName,
                        lastName  = signInInput.value.lastName,
                        phone = sessionUiState.value.phone
                    )
                )
                SignInUiState.Success.also { cb() }
            } catch(e: Exception) {
                println(e)
                SignInUiState.Error(e.localizedMessage)
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 2_000L
    }

    fun resetSignIn() {
        _signInInput.value = SignIn()
    }
}

interface SignInUiState {
    data object Loading: SignInUiState
    data object Success: SignInUiState
    data class Error(val message: String?): SignInUiState
}