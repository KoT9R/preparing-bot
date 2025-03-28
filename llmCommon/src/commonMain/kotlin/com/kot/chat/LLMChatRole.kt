package com.kot.chat

import kotlinx.serialization.Serializable

@Serializable
enum class LLMChatRole {
    user,
    assistant,
    system
}