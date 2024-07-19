package com.chat.radar.domain.repository

import com.chat.radar.common.AppResource
import com.chat.radar.data.model.Response
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getExample(): Flow<AppResource<Response>>
}
