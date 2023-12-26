package com.lomolo.uzi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.uzi.repository.SessionInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SessionViewModel(
    private val sessionRepository: SessionInterface
): ViewModel() {
    val sessionUiState: StateFlow<SessionState> = sessionRepository
        .getSession()
        .filterNotNull()
        .map {
            if (it.isNotEmpty()) {
                SessionState(
                    token = it[0].token,
                    id = it[0].id
                )
            } else {
                SessionState()
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = SessionState(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class SessionState(
    val token: String,
    val id: String
) {
    constructor(): this("", "")
}