package com.notkamui.pokemontcgsim.core

import com.notkamui.pokemontcgsim.core.constant.Errors
import com.notkamui.pokemontcgsim.core.response.MessageKeyDTO
import com.notkamui.pokemontcgsim.core.response.asMessage

open class InternalDefinedException(val error: MessageKeyDTO) : Exception(error.key) {
    constructor(key: String) : this(MessageKeyDTO(key))
}

class InvalidUUIDException : InternalDefinedException(Errors.Parsing.INVALID_UUID)

class MissingParameterException(parameter: String) : InternalDefinedException(Errors.Parameters.MISSING_PARAMETER.asMessage("parameter" to parameter))

class MissingHeaderException(header: String) : InternalDefinedException(Errors.Headers.MISSING_HEADER.asMessage("header" to header))

class IllegalActionException(error: MessageKeyDTO = Errors.ILLEGAL_ACTION.asMessage()) : InternalDefinedException(error) {
    constructor(key: String) : this(MessageKeyDTO(key))
}

class PlayerNotFoundException : InternalDefinedException(Errors.Players.NOT_FOUND)