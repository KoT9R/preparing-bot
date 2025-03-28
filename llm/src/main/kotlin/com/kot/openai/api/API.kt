package com.kot.openai.api

import com.kot.chat.LLMMessage
import kotlinx.serialization.Serializable

object OpenAIAPI {
    @Serializable
    data class RequestAPI(
        val model: String,
        val messages: List<LLMMessage>
    )

    @Serializable
    data class ResponseAPI(
        val choices: List<Choice>
    ) {
        @Serializable
        data class Choice(
            val message: LLMMessage
        )
    }
}

