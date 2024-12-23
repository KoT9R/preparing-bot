package com.kot.com.kot.telegram.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.kot.openai.*

object MessageHandler {
    fun registerHandlers(dispatcher: Dispatcher) {
        dispatcher.message {
            val messageText = update.message?.text ?: return@message
            val chatId = update.message?.chat?.id ?: return@message

            val result = OpenAIClient.get(messageText.toMessages())

            //check the message
            //send to open ai

            bot.sendMessage(ChatId.fromId(chatId), result.choices.first().message.toMessage() ?: "Error")
        }
    }

    private fun String.toMessages(): List<LLMMessage> {
        return listOf(
            User(
                content = listOf(
                    User.Content.Text(text = this)
                )
            )
        )
    }

    private fun LLMMessage.toMessage(): String? {
        return (this as? Assistant)?.content
    }
}