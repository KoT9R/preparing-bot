package com.kot.openai

import com.kot.chat.LLMMessage
import com.kot.openai.api.OpenAIAPI
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object OpenAIClient {
    private val httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(SSE)
        install(HttpTimeout) {
            requestTimeoutMillis = 100000
            connectTimeoutMillis = 50000
            socketTimeoutMillis = 150000
        }
        defaultRequest {
            header(HttpHeaders.Authorization, OpenAIConf.authorization)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(OpenAIConf.Const.OPENAI_ORGANIZATION, OpenAIConf.organization)
        }
        engine {
            maxConnectionsCount = 1000
            endpoint {
                connectTimeout = 5000
                requestTimeout = 15000
                keepAliveTime = 30000
            }
        }
    }

    suspend fun get(messages: List<LLMMessage>): OpenAIAPI.ResponseAPI {
        val requestAPI = OpenAIAPI.RequestAPI("gpt-4o", messages)
        //TODO: change to sse
        val result = httpClient.post(OpenAIConf.Const.URL) {
            setBody(requestAPI)
        }
        println(result.body<OpenAIAPI.ResponseAPI>())
        return result.body()
    }
}