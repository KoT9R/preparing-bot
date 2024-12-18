package com.kot.com.kot.telegram.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

object MessageHandler {
    fun registerHandlers(dispatcher: Dispatcher) {
        dispatcher.message {
            val messageText = update.message?.text ?: return@message
            val chatId = update.message?.chat?.id ?: return@message

            //check the message
            //send to open ai

            bot.sendMessage(ChatId.fromId(chatId), "Hello, World!")
        }
    }
}