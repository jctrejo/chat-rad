package com.chat.radar.domain.remote

import com.chat.radar.data.model.Response
import retrofit2.http.POST

interface Service {

    @POST("example")
    suspend fun example(): Response
}
