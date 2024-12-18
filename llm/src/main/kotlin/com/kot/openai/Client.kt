package com.kot.openai

import com.kot.com.kot.openai.LLMMessage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.reflect.*
import java.net.URL

object OpenAIClient {
    private val httpClient = HttpClient()

    suspend fun get(messages: List<LLMMessage>): OpenAIAPI.ResponseAPI {
        val requestBuilder = HttpRequestBuilder(URL(OpenAIAPI.BASE_URL))
        val result = httpClient.get(requestBuilder)
        return result.body(TypeInfo(OpenAIAPI.ResponseAPI::class))
    }
}