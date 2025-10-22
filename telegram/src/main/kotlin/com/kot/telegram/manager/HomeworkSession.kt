package com.kot.telegram.manager

import com.kot.chat.ImageBase64


data class HomeworkSession(
    var state: HomeworkState = HomeworkState.WAITING_FOR_TASK,
    var taskPhoto: ImageBase64? = null,
    val solutionPhotos: MutableList<ImageBase64> = mutableListOf()
)