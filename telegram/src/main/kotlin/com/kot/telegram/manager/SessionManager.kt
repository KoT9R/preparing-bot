package com.kot.telegram.manager

import com.kot.openai.OpenAIClient
import com.kot.telegram.Bot
import com.kot.telegram.client.TgClient
import com.kot.telegram.prompt.Prompt
import com.kot.telegram.utils.Buttons
import com.kot.telegram.utils.toAssistantMessage
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.message.content.PhotoContent
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.utils.row
import com.kot.chat.User as LLMUser
import com.kot.chat.System as LLMSystem

object SessionManager {
    private val sessions = mutableMapOf<RawChatId, HomeworkSession>()

    fun startSession(chatId: RawChatId) {
        sessions[chatId] = HomeworkSession()
    }

    fun getSession(chatId: RawChatId): HomeworkSession? {
        return sessions[chatId]
    }

    fun endSession(chatId: RawChatId): HomeworkSession? {
        return sessions.remove(chatId)
    }

    suspend fun registerPhotoListener(builder: BehaviourContext) = with(builder) {
        onContentMessage { message ->
            val chat = message.chat
            val chatId = chat.id.chatId
            val session = sessions[chatId] ?: return@onContentMessage

            val photoContent = message.content as? PhotoContent ?: return@onContentMessage
            val fileId = photoContent.media.fileId.fileId
            val photoBase64 = TgClient.getFileAsBase64(fileId, Bot.token) ?: return@onContentMessage

            when (session.state) {
                HomeworkState.WAITING_FOR_TASK -> {
                    session.taskPhoto = photoBase64
                    session.state = HomeworkState.WAITING_FOR_SOLUTIONS
                    sendTextMessage(
                        chat, """
                        Фото задачи получено! Теперь отправь свои решения!
                    """.trimIndent()
                    )
                }

                HomeworkState.WAITING_FOR_SOLUTIONS -> {
                    session.solutionPhotos.add(photoBase64)
                    val keyboardMarkup = inlineKeyboard {
                        row { dataButton("Закончить проверку", Buttons.Names.END) }
                    }
                    sendTextMessage(
                        chat = chat,
                        text = "Твое решение получено! Отправь еще или закончи проверку.",
                        replyMarkup = keyboardMarkup
                    )
                }

                HomeworkState.COMPLETED -> {
                    sendTextMessage(chat, "Твои задачи находятся в обработке!)")
                }
            }
        }
    }

    suspend fun processHomework(session: HomeworkSession): String {
        val messages = mutableListOf<LLMUser.Content>()
        val taskPhoto = session.taskPhoto?.let {
            LLMUser.Content.ImageUrl(imageUrl = LLMUser.Content.ImageUrl.ImageUrlDetails(it))
        } ?: return "Невозможно проверить решения без условия задачи"
        val taskDescription = LLMUser.Content.Text(
            """
                    Привет! Я ученик! Первое фото - это задача. Все последующие - это мои решения. Объясни мне мои ошибки или похвали меня если я сделал все хорошо!
                """.trimIndent()
        )
        val solutionPhotos = mutableListOf<LLMUser.Content.ImageUrl>()
        session.solutionPhotos.forEach { base64 ->
            solutionPhotos.add(LLMUser.Content.ImageUrl(imageUrl = LLMUser.Content.ImageUrl.ImageUrlDetails(base64)))
        }
        messages.add(taskDescription)
        messages.add(taskPhoto)
        messages.addAll(solutionPhotos)

        val userMessage = LLMUser(content = messages)

        val result = OpenAIClient.get(
            listOf(LLMSystem(Prompt.system), userMessage)
        )
        return result.toAssistantMessage()
    }
}