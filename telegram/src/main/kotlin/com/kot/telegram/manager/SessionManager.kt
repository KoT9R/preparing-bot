package com.kot.telegram.manager

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.kot.openai.OpenAIClient
import com.kot.openai.chat.System
import com.kot.openai.chat.User
import com.kot.telegram.Bot
import com.kot.telegram.client.TgClient
import com.kot.telegram.prompt.Prompt
import com.kot.telegram.utils.Buttons
import com.kot.telegram.utils.toAssistantMessage

object SessionManager {
    private val sessions = mutableMapOf<Long, HomeworkSession>()

    fun startSession(chatId: Long) {
        sessions[chatId] = HomeworkSession()
    }

    fun getSession(chatId: Long): HomeworkSession? {
        return sessions[chatId]
    }

    fun endSession(chatId: Long): HomeworkSession? {
        return sessions.remove(chatId)
    }

    fun registerPhotoListener(dispatcher: Dispatcher) {
        dispatcher.message {
            val chatId = message.chat.id
            val photoList = message.photo ?: return@message
            val session = sessions[chatId] ?: return@message
            val fileId = photoList.last().fileId

            val photoBase64 = TgClient.getFileAsBase64(fileId, Bot.token) ?: return@message
            bot.sendChatAction(ChatId.fromId(chatId), ChatAction.UPLOAD_PHOTO)

            when (session.state) {
                HomeworkState.WAITING_FOR_TASK -> {
                    session.taskPhoto = photoBase64
                    session.state = HomeworkState.WAITING_FOR_SOLUTIONS
                    bot.sendMessage(
                        ChatId.fromId(chatId), """
                        Фото задачи получено! Теперь отправь свои решения!
                    """.trimIndent()
                    )
                }

                HomeworkState.WAITING_FOR_SOLUTIONS -> {
                    session.solutionPhotos.add(photoBase64)
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "Твое решение получено! Отправь еще или закончи проверку.",
                        replyMarkup = InlineKeyboardMarkup.create(listOf(Buttons.end))
                    )
                }

                HomeworkState.COMPLETED -> {
                    bot.sendMessage(ChatId.fromId(chatId), "Твои задачи находятся в обработке!)")
                }
            }
        }
    }

    suspend fun processHomework(session: HomeworkSession): String {
        val messages = mutableListOf<User.Content>()
        val taskPhoto = session.taskPhoto?.let {
            User.Content.ImageUrl(imageUrl = User.Content.ImageUrl.ImageUrlDetails(it))
        } ?: return "Невозможно проверить решения без условия задачи"
        val taskDescription = User.Content.Text(
            """
                    Привет! Я ученик! Первое фото - это задача. Все последующие - это мои решения. Объясни мне мои ошибки или похвали меня если я сделал все хорошо!
                """.trimIndent()
        )
        val solutionPhotos = mutableListOf<User.Content.ImageUrl>()
        session.solutionPhotos.forEachIndexed { _, base64 ->
            solutionPhotos.add(User.Content.ImageUrl(imageUrl = User.Content.ImageUrl.ImageUrlDetails(base64)))
        }
        messages.add(taskDescription)
        messages.add(taskPhoto)
        messages.addAll(solutionPhotos)

        val userMessage = User(content = messages)

        val result = OpenAIClient.get(
            listOf(System(Prompt.system), userMessage)
        )
        return result.toAssistantMessage()
    }
}