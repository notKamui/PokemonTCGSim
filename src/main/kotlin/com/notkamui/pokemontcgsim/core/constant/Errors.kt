package com.notkamui.pokemontcgsim.core.constant

object Errors {

    private const val PREFIX = "errors"

    const val UNKNOWN = "$PREFIX.unknown"
    const val ILLEGAL_ACTION = "$PREFIX.illegal_action"
    const val BAD_REQUEST_RAW = "$PREFIX.bad_request_raw"
    const val NOT_FOUND_RAW = "$PREFIX.not_found_raw"
    const val UNKNOWN_MESSAGE = "$PREFIX.unknown_message"

    object Headers {

        private const val PREFIX = "${Errors.PREFIX}.headers"

        const val MISSING_HEADER = "$PREFIX.missing_header"
    }

    object Parameters {

        private const val PREFIX = "${Errors.PREFIX}.parameters"

        const val MISSING_PARAMETER = "$PREFIX.missing_parameter"
    }

    object Parsing {

        private const val PREFIX = "${Errors.PREFIX}.parsing"

        const val INVALID_UUID = "$PREFIX.invalid_uuid"
    }

    object Players {

            private const val PREFIX = "${Errors.PREFIX}.player"

            const val NOT_FOUND = "$PREFIX.not_found"
    }
}