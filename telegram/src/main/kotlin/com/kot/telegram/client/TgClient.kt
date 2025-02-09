package com.kot.telegram.client

import com.kot.openai.chat.ImageBase64
import com.kot.telegram.api.FileResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object TgClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getFileAsBase64(fileId: String, token: String): ImageBase64? {
        val fileResponse: FileResponse = client.get("https://api.telegram.org/bot$token/getFile") {
            parameter("file_id", fileId)
        }.body()
        val filePath = fileResponse.result?.filePath ?: return null

        val fileBytes = client.get("https://api.telegram.org/file/bot$token/$filePath").body<ByteArray>()

        val content = Base64.encode(fileBytes)

        return ImageBase64(content)
    }
}