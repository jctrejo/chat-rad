package com.chat.radar.domain.repository

import com.chat.radar.common.AppResource
import com.chat.radar.data.model.Response
import com.chat.radar.domain.remote.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

open class RepositoryImpl @Inject constructor(
    private val api: Service
) : Repository {

    override suspend fun getExample(): Flow<AppResource<Response>> = flow {
        emit(AppResource.Loading)
        try {
            val response = api.example()
            emit(AppResource.Success(response))
        } catch (exception: HttpException) {
            emit(AppResource.Error(exception.code().toString()))

        } catch (exception: IOException) {
            emit(AppResource.Error(exception.toString()))
        }
    }
}
