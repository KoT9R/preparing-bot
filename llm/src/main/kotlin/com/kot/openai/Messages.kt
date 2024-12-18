package com.kot.com.kot.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class LLMMessages(val messages: List<LLMMessage>)

@Serializable
sealed class LLMMessage(val role: String)

@Serializable
data class User(val content: List<Content>) : LLMMessage("user") {
    @Serializable
    sealed class Content {
        @Serializable
        data class Text(
            val type: String = "text",
            val text: String
        ) : Content()

        @Serializable
        data class ImageUrl(
            val type: String = "image_url",
            @SerialName("image_url")
            val imageUrl: ImageUrlDetails
        ) : Content() {
            @Serializable
            data class ImageUrlDetails(
                val url: String,
                val detail: String = "auto"
            )
        }
    }
}

@Serializable
data class Assistant(val content: String) : LLMMessage("assistant")

@Serializable
data class System(val content: String) : LLMMessage("system")