package com.kot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.kot.telegram.manager.HomeworkState
import com.kot.telegram.manager.SessionManager
import com.kot.telegram.utils.Buttons
import com.kot.telegram.utils.Commands

object Bot {
    val token = System.getenv("BOT_TOKEN")

    private val botInstance = bot {
        token = Bot.token

        dispatch {
            command(Commands.START.command) {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = Commands.START.description
                )
            }

            command(Commands.HOMEWORK.command) {
                val keyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(Buttons.start, Buttons.end)
                )

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Сейчас тебе нужно будет отправлять фото твоих решалок. Сначала отправь фото твоей задачи, а затем твои решения.",
                    replyMarkup = keyboardMarkup
                )
            }

            callbackQuery(Buttons.Names.START) {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                SessionManager.startSession(chatId)
                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Пожалуйста приложи фото задачи!"
                )
            }

            callbackQuery(Buttons.Names.END) {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                val session = SessionManager.getSession(chatId)
                if (session == null) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "Ты пока не начал сессию проверки. Напиши в чат /homework"
                    )
                    return@callbackQuery
                } else if (session.state == HomeworkState.WAITING_FOR_TASK) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "Пожалуйста, приложи фото задачи!"
                    )
                } else {
                    session.state = HomeworkState.COMPLETED

                    val result = SessionManager.processHomework(session)
                    bot.sendMessage(chatId = ChatId.fromId(chatId), text = result)
                    SessionManager.endSession(chatId)
                }
            }

            SessionManager.registerPhotoListener(this)
        }
    }

    fun start() {
        botInstance.setMyCommands(
            listOf(
                BotCommand(Commands.START.command, "Инструкция"),
                BotCommand(Commands.HOMEWORK.command, "Отправка задач")
            )
        )
        botInstance.startPolling()
    }
}