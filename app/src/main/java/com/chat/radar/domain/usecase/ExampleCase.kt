package com.chat.radar.domain.usecase

import com.chat.radar.common.AppResource
import com.chat.radar.data.model.Response
import kotlinx.coroutines.flow.Flow

interface ExampleCase {
    suspend operator fun invoke(): Flow<AppResource<Response>>
}