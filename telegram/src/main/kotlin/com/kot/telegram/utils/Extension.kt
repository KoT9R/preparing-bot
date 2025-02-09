package com.kot.telegram.utils

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.kot.openai.chat.Assistant
import com.kot.openai.api.OpenAIAPI

fun OpenAIAPI.ResponseAPI.toAssistantMessage(): String = (choices.first().message as Assistant).content

fun Message.chatId(): ChatId = ChatId.fromId(chat.id)

fun Message?.chatIdOrError(): ChatId = this?.chatId() ?: error("Message is null, chat id is unavailable")