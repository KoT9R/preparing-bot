package com.kot.telegram

import com.kot.telegram.manager.HomeworkState
import com.kot.telegram.manager.SessionManager
import com.kot.telegram.utils.Buttons
import com.kot.telegram.utils.Commands
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

object Bot {
    val token = System.getenv("BOT_TOKEN") ?: error("No token provided")

    private val botInstance = telegramBot(token)

    suspend fun start() = coroutineScope {
        botInstance.buildBehaviourWithLongPolling(scope = this) {
            // Bot commands
            setMyCommands(
                listOf(
                    BotCommand(Commands.START.command, "Инструкция"),
                    BotCommand(Commands.HOMEWORK.command, "Отправка задач")
                )
            )

            onCommand(Commands.START.command) {
                sendTextMessage(chat = it.chat, text = Commands.START.description)
            }

            onCommand(Commands.HOMEWORK.command) {
                val keyboardMarkup = inlineKeyboard {
                    row {
                        dataButton("Начать проверку", Buttons.Names.START)
                        dataButton("Закончить проверку", Buttons.Names.END)
                    }
                }
                sendTextMessage(
                    chat = it.chat,
                    text = "Сейчас тебе нужно будет отправлять фото твоих решалок. Сначала отправь фото твоей задачи, а затем твои решения.",
                    replyMarkup = keyboardMarkup
                )
            }

            onDataCallbackQuery(Buttons.Names.START) { callback ->
                val chat = callback.message?.chat ?: return@onDataCallbackQuery
                SessionManager.startSession(chat.id.chatId)
                sendTextMessage(
                    chat = chat,
                    text = "Пожалуйста приложи фото задачи!"
                )
            }

            onDataCallbackQuery(Buttons.Names.END) { callback ->
                val chat = callback.message?.chat ?: return@onDataCallbackQuery
                val chatId = chat.id.chatId
                val session = SessionManager.getSession(chatId)
                if (session == null) {
                    sendTextMessage(
                        chat = chat,
                        text = "Ты пока не начал сессию проверки. Напиши в чат /homework"
                    )
                    return@onDataCallbackQuery
                } else if (session.state == HomeworkState.WAITING_FOR_TASK) {
                    sendTextMessage(
                        chat = chat,
                        text = "Пожалуйста, приложи фото задачи!"
                    )
                } else {
                    session.state = HomeworkState.COMPLETED

                    val result = SessionManager.processHomework(session)
                    sendTextMessage(chat = chat, text = result)
                    SessionManager.endSession(chatId)
                }
            }

            // Register photo listener
            SessionManager.registerPhotoListener(this)
        }.join()
    }
}