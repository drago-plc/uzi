package com.lomolo.uzi.repository

import com.lomolo.uzi.model.Session
import com.lomolo.uzi.model.SignIn
import com.lomolo.uzi.network.UziRestApiService
import com.lomolo.uzi.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface AuthSession {
    fun getSession(): Flow<List<Session>>
    suspend fun signIn(input: SignIn): Session
}

class SessionRepository(
    private val sessionDao: SessionDao,
    private val uziRestApiService: UziRestApiService
): AuthSession {
    override fun getSession() = sessionDao.getSession()
    override suspend fun signIn(input: SignIn): Session {
        val res = uziRestApiService.signIn(input)
        sessionDao.createSession(session = Session(id = res.id, token = res.token))
        return res
    }
}