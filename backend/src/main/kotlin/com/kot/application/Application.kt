package com.kot.application

import com.kot.com.kot.telegram.Bot
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    launch {
        Bot.start()
    }
}
