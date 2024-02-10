package com.lomolo.uzi.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lomolo.uzi.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTrip(input: Trip)
    @Query("SELECT * FROM trips LIMIT 1")
    fun getTrip(): Flow<Trip>
    @Update
    suspend fun updateTrip(trip: Trip)
}