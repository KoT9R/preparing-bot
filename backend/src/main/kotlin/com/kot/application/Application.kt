package com.kot.application

import com.kot.telegram.Bot
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val botScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName("BotScope"))

    monitor.subscribe(ApplicationStarted) {
        botScope.launch { Bot.start() }
    }

    monitor.subscribe(ApplicationStopping) {
        botScope.cancel()
    }
}
