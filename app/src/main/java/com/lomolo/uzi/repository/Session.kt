package com.lomolo.uzi.repository

import com.lomolo.uzi.model.Session
import com.lomolo.uzi.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface AuthSession {
    suspend fun createSession(session: Session)
    fun getSession(): Flow<List<Session>>
}

class SessionRepository(
    private val sessionDao: SessionDao
): AuthSession {
    override suspend fun createSession(session: Session) = sessionDao.createSession(session)
    override fun getSession() = sessionDao.getSession()
}