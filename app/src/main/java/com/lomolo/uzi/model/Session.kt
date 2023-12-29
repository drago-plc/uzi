package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = false) val id: String = "",
    val token: String = "",
    val status: Status = Status.OFFLINE,
    val isCourier: Boolean = false
)

enum class Status{OFFLINE, ONLINE}