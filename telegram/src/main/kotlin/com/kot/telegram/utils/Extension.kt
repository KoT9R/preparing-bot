package com.kot.telegram.utils

import com.kot.chat.Assistant
import com.kot.openai.api.OpenAIAPI

fun OpenAIAPI.ResponseAPI.toAssistantMessage(): String = (choices.first().message as Assistant).content