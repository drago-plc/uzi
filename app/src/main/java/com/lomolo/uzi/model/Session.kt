package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey val id: String = "",
    val firstname: String = "",
    val lastname : String = "",
    val token: String = "",
    val courierStatus: CourierStatus = CourierStatus.ONBOARDING,
    val isCourier: Boolean = false,
    val phone: String = "",
    val onboarding: Boolean = false
)

enum class CourierStatus{OFFLINE, ONLINE, ONBOARDING}