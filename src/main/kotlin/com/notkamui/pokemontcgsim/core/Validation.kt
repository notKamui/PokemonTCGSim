package com.notkamui.pokemontcgsim.core

import com.notkamui.pokemontcgsim.core.constant.Errors
import com.notkamui.pokemontcgsim.core.response.ActionResponse
import com.notkamui.pokemontcgsim.core.response.DTO
import com.notkamui.pokemontcgsim.core.response.MessageDTO
import com.notkamui.pokemontcgsim.core.response.MessageKeyDTO
import com.notkamui.pokemontcgsim.core.response.Response
import com.notkamui.pokemontcgsim.core.response.ResultData
import com.notkamui.pokemontcgsim.core.response.asMessage
import io.ktor.http.HttpStatusCode

internal inline fun <T, reified R : Response<T>> validate(
    errorTemplate: MessageDTO<out DTO> = MessageDTO.simple(Errors.UNKNOWN.asMessage()),
    defaultStatusCode: HttpStatusCode = HttpStatusCode.BadRequest,
    block: ValidationScope.() -> R
): R {
    val scope = ValidationScope()
    return try {
        val response = scope.block()
        if (scope.hasErrors()) createErrorResponse<T, R>(errorTemplate, defaultStatusCode, scope.getErrors())
        else response
    } catch (e: ValidationException) {
        createErrorResponse<T, R>(errorTemplate, defaultStatusCode, scope.getErrors())
    }
}

class ValidationException : Exception() {

    override fun fillInStackTrace(): Throwable = this
}

class ValidationScope {

    private val errors: MutableMap<String, Pair<MessageKeyDTO, HttpStatusCode?>> = mutableMapOf()

    fun raiseError(fieldName: String, error: MessageKeyDTO, statusCode: HttpStatusCode? = null) {
        errors[fieldName] = error to statusCode
    }

    fun raiseError(fieldName: String, error: String, statusCode: HttpStatusCode? = null) =
        raiseError(fieldName, error.asMessage(), statusCode)

    fun hasErrors() = errors.isNotEmpty()

    fun getErrors() = errors.toMap()

    fun catchErrors() {
        if (hasErrors()) {
            throw ValidationException()
        }
    }
}

private fun determineStatusCode(
    errors: Map<String, Pair<MessageKeyDTO, HttpStatusCode?>>,
    defaultStatusCode: HttpStatusCode
): HttpStatusCode {
    val statusCodes = errors.values.mapNotNull { it.second }.toSet()
    return when (statusCodes.size) {
        0 -> defaultStatusCode
        1 -> statusCodes.first()
        else -> HttpStatusCode.BadRequest
    }
}

private inline fun <T, reified R : Response<T>> createErrorResponse(
    errorTemplate: MessageDTO<out DTO>,
    defaultStatusCode: HttpStatusCode,
    errors: Map<String, Pair<MessageKeyDTO, HttpStatusCode?>>,
): R {
    val error = errorTemplate.copy(fields = errors.mapValues { it.value.first })
    val statusCode = determineStatusCode(errors, defaultStatusCode)
    return when (R::class) {
        ActionResponse::class -> ActionResponse<Nothing>(statusCode, error = ResultData(error))
        else -> Response<T>(statusCode, error = ResultData(error))
    } as R
}
