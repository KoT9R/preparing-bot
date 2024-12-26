package com.kot.openai.chat

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class LLMMessages(val messages: List<LLMMessage>)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("role")
@Serializable
sealed class LLMMessage {
    abstract val type: LLMChatRole
}

@Serializable
@SerialName("user")
data class User(
    val content: List<Content>
) : LLMMessage() {
    override val type: LLMChatRole = LLMChatRole.user
    @Serializable
    sealed class Content {
        @Serializable
        @SerialName("text")
        data class Text(
            val text: String
        ) : Content()

        @Serializable
        @SerialName("image_url")
        data class ImageUrl(
            @SerialName("image_url")
            val imageUrl: ImageUrlDetails
        ) : Content() {
            @Serializable
            data class ImageUrlDetails(
                val url: String,
                val detail: String = "auto"
            ) {
                constructor(image64Encoded: ImageBase64): this(url = "data:image/jpeg;base64,${image64Encoded.content}")
            }
        }
    }
}

@Serializable
@SerialName("assistant")
data class Assistant(
    val content: String
) : LLMMessage() {
    override val type: LLMChatRole = LLMChatRole.assistant
}

@Serializable
@SerialName("system")
data class System(
    val content: String
) : LLMMessage() {
    override val type: LLMChatRole = LLMChatRole.system
}