package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = false) val id: String = "",
    val token: String = "",
    val courierStatus: CourierStatus = CourierStatus.OFFLINE,
    val isCourier: Boolean = false,
    val onboarding: Boolean = false
)

enum class CourierStatus{OFFLINE, ONLINE}