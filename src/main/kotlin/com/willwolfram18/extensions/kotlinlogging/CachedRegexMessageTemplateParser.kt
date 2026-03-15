package com.willwolfram18.extensions.kotlinlogging

import com.fasterxml.jackson.module.kotlin.*

class CachedRegexMessageTemplateParser : MessageTemplateParser {
    companion object {
        private val fieldNameRegex = """\{([$@]?)(?<name>[a-zA-Z0-9]+)}""".toRegex()
        private val objectMapper = jacksonObjectMapper()
    }

    private val cache: MutableMap<String, List<Pair<String, String>>> = mutableMapOf()

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        val names = cache.getOrPut(messageTemplate) {
            fieldNameRegex.findAll(messageTemplate).map { Pair(it.groupValues[2], it.groupValues[1]) }.toList()
        }
        return names.zip(args.asIterable()).associate { (nameAndOperator, value) ->
            val (name, operator) = nameAndOperator
            name to when (operator) {
                "$" -> value?.toString()
                "@" -> destructure(value)
                else -> value
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
