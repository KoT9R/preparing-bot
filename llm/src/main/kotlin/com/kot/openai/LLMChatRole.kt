package com.kot.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class LLMChatRole {
    user,
    assistant,
    system
}