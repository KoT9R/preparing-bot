package com.kot.openai

import java.lang.System


object OpenAIConf {
    val authorization = "Bearer ${System.getenv("OPENAI_TOKEN")!!}"
    val organization = System.getenv("OPENAI_ORGANIZATION")!!

    object Const {
        const val OPENAI_ORGANIZATION = "OpenAI-Organization"
        const val URL = "https://api.openai.com/v1/chat/completions"
    }


}