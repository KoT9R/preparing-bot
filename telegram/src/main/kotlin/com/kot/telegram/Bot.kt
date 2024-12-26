package com.kot.com.kot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.kot.telegram.handlers.CommandHandler
import com.kot.telegram.handlers.MessageHandler
import com.kot.telegram.handlers.PhotoHandler

object Bot {
    val token = System.getenv("BOT_TOKEN")

    val botInstance = bot {
        token = this@Bot.token
        dispatch {
            MessageHandler.registerHandlers(this)
            CommandHandler.registerHandlers(this)
            PhotoHandler.registerHandlers(this)
        }
    }

    fun start() {
        botInstance.startPolling()
    }
}