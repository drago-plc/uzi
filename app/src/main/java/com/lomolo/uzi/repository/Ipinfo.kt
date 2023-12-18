package com.lomolo.uzi.repository

import com.lomolo.uzi.model.Ipinfo
import com.lomolo.uzi.network.UziRestApiService

interface IpinfoRepository {
    suspend fun getIpinfo(): Ipinfo
}

class DeviceIpinfoRepository(
    private val restApiService: UziRestApiService
): IpinfoRepository {
    override suspend fun getIpinfo(): Ipinfo = restApiService.getIpinfo()
}