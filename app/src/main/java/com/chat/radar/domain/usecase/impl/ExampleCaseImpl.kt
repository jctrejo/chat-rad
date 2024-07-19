package com.chat.radar.domain.usecase.impl

import com.chat.radar.common.AppResource
import com.chat.radar.data.model.Response
import com.chat.radar.domain.repository.Repository
import com.chat.radar.domain.usecase.ExampleCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExampleCaseImpl @Inject constructor(private val repository: Repository) :
    ExampleCase {

    override suspend operator fun invoke(): Flow<AppResource<Response>> = repository.getExample()
}
