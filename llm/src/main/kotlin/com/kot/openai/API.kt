package com.kot.openai

import com.kot.com.kot.openai.LLMMessage
import kotlinx.serialization.Serializable

object OpenAIAPI {
    const val BASE_URL = "https://api.openai.com/v1/"

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

