package com.notkamui.pokemontcgsim.plugins

import com.notkamui.pokemontcgsim.core.constant.Errors
import com.notkamui.pokemontcgsim.core.IllegalActionException
import com.notkamui.pokemontcgsim.core.InternalDefinedException
import com.notkamui.pokemontcgsim.core.InvalidUUIDException
import com.notkamui.pokemontcgsim.core.MissingHeaderException
import com.notkamui.pokemontcgsim.core.MissingParameterException
import com.notkamui.pokemontcgsim.core.PlayerNotFoundException
import com.notkamui.pokemontcgsim.core.response.MessageDTO
import com.notkamui.pokemontcgsim.core.response.Response
import com.notkamui.pokemontcgsim.core.response.asMessage
import com.notkamui.pokemontcgsim.core.response.respondNoSuccess
import com.notkamui.pokemontcgsim.util.Environment
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.SerializationException


fun Application.configureExceptionAdvisors() {
    install(StatusPages) {
        exception(::handleException)
    }
}

private suspend fun handleException(call: ApplicationCall, cause: Throwable) {
    if (Environment.isDev) {
        call.application.environment.log.info("[DEV] Exception caught: ${cause.javaClass.name}", cause)
    }

    when (cause) {
        is MissingParameterException,
        is MissingHeaderException,
        is SerializationException,
        is IllegalArgumentException,
        is InvalidUUIDException,
        is BadRequestException -> badRequestMessage(call, cause)

        is IllegalActionException -> forbiddenMessage(call, cause)

        is PlayerNotFoundException,
        is NotFoundException -> notFoundMessage(call, cause)

        else -> unhandledError(call, cause)
    }
}

private suspend fun badRequestMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is InternalDefinedException -> call.respondNoSuccess(Response.badRequest(cause.error))
    is BadRequestException -> call.respondNoSuccess(Response.badRequest(MessageDTO.simple(
        title = Errors.BAD_REQUEST_RAW.asMessage(),
        message = (cause.message ?: cause.javaClass.name).asMessage(),
    )))
    else -> call.respondNoSuccess(Response.badRequest(cause.message ?: Errors.UNKNOWN))
}

private suspend fun unauthorizedMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is InternalDefinedException -> call.respondNoSuccess(Response.unauthorized(cause.error))
    else -> call.respondNoSuccess(Response.unauthorized(Errors.UNKNOWN))
}

private suspend fun forbiddenMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is InternalDefinedException -> call.respondNoSuccess(Response.forbidden(cause.error))
    else -> call.respondNoSuccess(Response.forbidden(Errors.UNKNOWN))
}

private suspend fun notFoundMessage(call: ApplicationCall, cause: Throwable) = when (cause) {
    is InternalDefinedException -> call.respondNoSuccess(Response.notFound(cause.error))
    is NotFoundException -> call.respondNoSuccess(Response.notFound(MessageDTO.simple(
        title = Errors.NOT_FOUND_RAW.asMessage(),
        message = (cause.message ?: cause.javaClass.name).asMessage(),
    )))
    else -> call.respondNoSuccess(Response.notFound(cause.message ?: Errors.UNKNOWN))
}

private suspend fun unhandledError(call: ApplicationCall, cause: Throwable) {
    call.respondNoSuccess(Response.error(
        HttpStatusCode.InternalServerError,
        MessageDTO.simple(
            title = Errors.UNKNOWN.asMessage(),
            message = cause.message?.let { Errors.UNKNOWN_MESSAGE.asMessage("hint" to it) }
        )
    ))
    call.application.log.error("Unexpected error", cause)
}