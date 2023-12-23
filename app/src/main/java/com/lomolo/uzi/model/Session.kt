package com.lomolo.uzi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    val token: String,
    @PrimaryKey
    val id: String
)
