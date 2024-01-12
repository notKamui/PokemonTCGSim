@file:UseSerializers(UUIDSerializer::class)

package com.notkamui.pokemontcgsim.core.response

import com.notkamui.pokemontcgsim.plugins.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

interface DTO

@Serializable
data class MessageKeyDTO(
    val key: String,
    val parameters: Map<String, MessageKeyDTO>? = null,
) : DTO {
    constructor(key: String, vararg parameters: Pair<String, MessageKeyDTO>) : this(key, mapOf(*parameters))

    fun withParameters(vararg parameters: Pair<String, String>): MessageKeyDTO {
        val params = parameters.map { (k, v) -> k to MessageKeyDTO(v) }.toTypedArray()
        if (this.parameters != null) {
            return copy(parameters = this.parameters.plus(params))
        }
        return copy(parameters = params.toMap())
    }
}

fun String.asMessage() = MessageKeyDTO(this)
fun String.asMessage(vararg parameters: Pair<String, String>) = MessageKeyDTO(this, parameters.associate { (k, v) -> k to MessageKeyDTO(v) })

@Serializable
data class MessageDTO<T: DTO>(
    val title: MessageKeyDTO,
    val message: MessageKeyDTO?,
    val payload: T?,
    val fields: Map<String, MessageKeyDTO>?,
) : DTO {

    companion object {
        fun simple(
            title: MessageKeyDTO,
            message: MessageKeyDTO? = null,
            fields: Map<String, MessageKeyDTO>? = null
        ): MessageDTO<Nothing> =
            MessageDTO(title, message, null, fields)

        fun <T: DTO> payload(
            title: MessageKeyDTO,
            payload: T,
            message: MessageKeyDTO? = null,
            fields: Map<String, MessageKeyDTO>? = null
        ): MessageDTO<T> =
            MessageDTO(title, message, payload, fields)
    }

    fun withFields(vararg fields: Pair<String, MessageKeyDTO>): MessageDTO<T> =
        copy(fields = fields.toMap())

}
