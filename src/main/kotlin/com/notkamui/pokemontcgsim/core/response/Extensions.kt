package com.notkamui.pokemontcgsim.core.response

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond as ktorRespond

suspend inline fun <reified S> ApplicationCall.respond(response: Response<S>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            result.data?.let { ktorRespond(it) }
        },
        { result ->
            this.response.status(response.status)
            result.data?.let { ktorRespond(it) }
        }
    )
}

suspend inline fun ApplicationCall.respondNoSuccess(response: Response<Nothing>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        { result ->
            this.response.status(response.status)
            result.data?.let { ktorRespond(it) }
        }
    )
}

suspend inline fun <reified S> ApplicationCall.respondNoError(response: Response<S>) {
    response.ifSuccessOrElse(
        { result ->
            this.response.status(response.status)
            result.data?.let { ktorRespond(it) }
        },
        {
            this.response.status(response.status)
        }
    )
}

suspend inline fun ApplicationCall.respondNothing(response: Response<Nothing>) {
    response.ifSuccessOrElse(
        {
            this.response.status(response.status)
        },
        {
            this.response.status(response.status)
        }
    )
}
