package com.notkamui.pokemontcgsim.util

import com.notkamui.pokemontcgsim.core.InvalidUUIDException
import com.notkamui.pokemontcgsim.core.MissingHeaderException
import com.notkamui.pokemontcgsim.core.MissingParameterException
import io.ktor.server.application.ApplicationCall
import java.util.UUID

private val SHORT_UUID_REGEX = """^(\w{8})(\w{4})(\w{4})(\w{4})(\w{12})$""".toRegex()

fun ApplicationCall.getHeader(header: String): String = request.headers[header] ?: throw MissingHeaderException(header)

fun ApplicationCall.getParamOrNull(name: String): String? = parameters[name]
fun ApplicationCall.getParam(name: String): String = parameters[name] ?: throw MissingParameterException(name)

fun String.toUUIDOrNull() = try { UUID.fromString(this) } catch (e: IllegalArgumentException) { null }
fun String.toUUID(): UUID = try { UUID.fromString(this) } catch (e: IllegalArgumentException) { throw MissingParameterException(this) }
fun String.toUUIDShort(): UUID {
    val uuid = this.replaceFirst(SHORT_UUID_REGEX, "$1-$2-$3-$4-$5")
    return uuid.toUUID()
}
fun ApplicationCall.getUUIDOrNull(name: String = "uuid"): UUID? = getParamOrNull(name)?.toUUIDOrNull()
fun ApplicationCall.getUUID(name: String = "uuid"): UUID = getUUIDOrNull(name) ?: throw InvalidUUIDException()
