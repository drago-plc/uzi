package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip (
    @PrimaryKey val id: String = "",
    val status: String = TripStatus.CREATE.toString(),
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

enum class TripStatus {
    COURIER_EN_ROUTE,
    CANCELLED,
    CREATE,
    COMPLETE,
    COURIER_ARRIVING,
    COURIER_FOUND,
    COURIER_NOT_FOUND
}