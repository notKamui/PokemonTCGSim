package com.notkamui.pokemontcgsim

import com.notkamui.pokemontcgsim.plugins.configureExceptionAdvisors
import com.notkamui.pokemontcgsim.plugins.configureHTTP
import com.notkamui.pokemontcgsim.plugins.configureRouting
import com.notkamui.pokemontcgsim.plugins.configureSerialization
import com.notkamui.pokemontcgsim.util.Environment
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import java.util.TimeZone

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused") // Referenced in application.yaml
fun Application.module() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    Environment.application = this
    if (Environment.isDev) log.info("Running in development mode")

    configureExceptionAdvisors()
    configureHTTP()
    configureSerialization()
    configureRouting()
}
