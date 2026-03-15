package com.willwolfram18.extensions.kotlinlogging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.willwolfram18.extensions.kotlinlogging.RegexMessageTemplateParser.Companion.objectMapper

class RegexMessageTemplateParser : MessageTemplateParser {
    companion object {
        private val fieldNameRegex = """\{([$@]?)(?<name>[a-zA-Z0-9]+)}""".toRegex()
        private val objectMapper = jacksonObjectMapper()
    }

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        val namedArgumentMatches = fieldNameRegex.findAll(messageTemplate)
        val namedArgumentPairs = namedArgumentMatches.zip(args.asSequence())

        return buildMap {
            for ((match, value) in namedArgumentPairs) {
                val operator = match.groupValues[1]
                val name = match.groupValues[2]
                val finalValue = when (operator) {
                    "$" -> value?.toString()
                    "@" -> destructure(value)
                    else -> value
                }
                put(name, finalValue)
            }
        }
    }

    private fun destructure(value: Any?): Any? {
        return when (value) {
            null -> null
            is List<*>,
            is String,
            is Number,
            is Boolean,
            is Map<*, *> -> value

            is Array<*> -> value.toList()
            is Sequence<*> -> value.toList()

            else -> objectMapper.convertValue(value, Map::class.java)
        }
    }
}