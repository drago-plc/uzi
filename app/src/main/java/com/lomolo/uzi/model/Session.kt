package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey val id: String = "",
    @Json(name = "first_name") val firstname: String = "",
    @Json(name = "last_name") val lastname : String = "",
    val token: String = "",
    val courierStatus: CourierStatus = CourierStatus.ONBOARDING,
    val isCourier: Boolean = false,
    val phone: String = "",
    val onboarding: Boolean = false
)

enum class CourierStatus{OFFLINE, ONLINE, ONBOARDING}