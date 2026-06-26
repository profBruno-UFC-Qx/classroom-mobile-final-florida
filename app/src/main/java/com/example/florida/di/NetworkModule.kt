package com.example.florida.di

import android.util.Log
import com.example.florida.network.BASE_URL
import com.example.florida.network.FloridaApi
import com.example.florida.network.FloridaRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HTTP call", message)
                    }
                }
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    if (cause is ResponseException) {
                        val responseBody = runCatching { cause.response.bodyAsText() }.getOrDefault("")
                        throw IllegalStateException(
                            "http ${cause.response.status.value}: ${responseBody.ifBlank { cause.message.orEmpty() }}",
                            cause
                        )
                    }
                    throw cause
                }
            }
            defaultRequest {
                url.takeFrom(BASE_URL)
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
        }
    }

    @Provides
    @Singleton
    fun provideFloridaApi(httpClient: HttpClient): FloridaApi {
        return FloridaApi(httpClient)
    }

    @Provides
    @Singleton
    fun provideFloridaRemoteRepository(api: FloridaApi): FloridaRemoteRepository {
        return FloridaRemoteRepository(api)
    }
}
