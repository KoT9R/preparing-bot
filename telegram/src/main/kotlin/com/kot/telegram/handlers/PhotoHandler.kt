package com.kot.com.kot.telegram.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile

object PhotoHandler {
    fun registerHandlers(dispatcher: Dispatcher) {
        dispatcher.message {
            val chatId = update.message?.chat?.id ?: return@message
            val photo = update.message?.photo ?: return@message

            //check the message
            //send to open ai

            bot.sendPhoto(ChatId.fromId(chatId), TelegramFile.ByFileId(photo.first().fileId), caption = "Your photo")
        }
    }
}