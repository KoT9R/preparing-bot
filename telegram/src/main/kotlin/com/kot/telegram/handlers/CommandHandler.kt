package com.kot.com.kot.telegram.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

object CommandHandler {
    fun registerHandlers(dispatcher: Dispatcher) {
        dispatcher.command("start") {
            val chatId = message.chat.id
            bot.sendMessage(ChatId.fromId(chatId), "Let's start!")
        }

        dispatcher.command("help") {
            val chatId = message.chat.id
            bot.sendMessage(ChatId.fromId(chatId), "Help!")
        }
    }
}