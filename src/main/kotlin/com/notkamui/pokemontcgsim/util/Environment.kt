package com.notkamui.pokemontcgsim.util

import io.ktor.http.ContentType
import io.ktor.server.application.Application

object Environment {
    lateinit var application: Application
    private  val config get() = application.environment.config

    private val envKind get() = config.property("ktor.environment").getString()
    val isDev get() = envKind == "dev"
    val isProd get() = envKind == "prod"
}
