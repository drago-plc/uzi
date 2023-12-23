package com.lomolo.uzi.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lomolo.uzi.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createSession(session: Session)
    @Query("SELECT * FROM sessions LIMIT 1")
    fun getSession(): Flow<List<Session>>
}
