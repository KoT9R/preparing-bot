package com.kot.telegram.utils

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

object Buttons {
    val start = InlineKeyboardButton.CallbackData(
        text = "Начать проверку",
        callbackData = Names.START
    )
    val end = InlineKeyboardButton.CallbackData(
        text = "Закончить проверку",
        callbackData = Names.END
    )
    object Names {
        const val START = "startHomework"
        const val END = "endHomework"
    }
}