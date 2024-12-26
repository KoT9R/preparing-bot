package com.kot.com.kot.telegram.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.kot.com.kot.telegram.Bot
import com.kot.openai.OpenAIClient
import com.kot.openai.chat.ImageBase64
import com.kot.openai.chat.User
import com.kot.openai.chat.User.Content.ImageUrl.ImageUrlDetails
import com.kot.telegram.client.TgClient
import com.kot.telegram.utils.toAssistantMessage
import kotlin.io.encoding.ExperimentalEncodingApi

object PhotoHandler {
    data class TgPhoto(
        val photoBase64: ImageBase64,
        val caption: String? = null
    ) {
        fun toUserMessage(): User {
            return User(
                content = listOf(
                    User.Content.Text(text = caption ?: ""),
                    User.Content.ImageUrl(imageUrl = ImageUrlDetails(photoBase64))
                )
            )
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun registerHandlers(dispatcher: Dispatcher) {
        dispatcher.message {
            val chatId = update.message?.chat?.id ?: return@message
            //TODO: check subjects (like #math, #russian, #informatics and etc)
            val caption = update.message?.caption ?: return@message
            val photo = update.message?.photo ?: return@message

            //TODO: add logging
            val photoBase64 = TgClient.getFileAsBase64(photo.last().fileId, Bot.token) ?: return@message

            //check the message
            //send to open ai

            val llmMessage = listOf(TgPhoto(ImageBase64(photoBase64), caption).toUserMessage())

            val result = OpenAIClient.get(llmMessage)

            bot.sendMessage(ChatId.fromId(chatId), result.toAssistantMessage())
        }
    }
}