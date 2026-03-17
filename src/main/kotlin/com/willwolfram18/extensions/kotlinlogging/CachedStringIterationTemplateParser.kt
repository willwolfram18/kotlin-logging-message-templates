package com.willwolfram18.extensions.kotlinlogging

import com.fasterxml.jackson.module.kotlin.*

class CachedStringIterationTemplateParser : MessageTemplateParser {
    companion object {
        const val PROPERTY_START = '{'
        const val PROPERTY_END = '}'
        private val objectMapper = jacksonObjectMapper()
    }

    private val cache: MutableMap<String, List<Pair<String, String>>> = mutableMapOf()

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        if (messageTemplate.isEmpty()) {
            return emptyMap()
        }

        val names = cache.getOrPut(messageTemplate) {
            locatePropertyNames(messageTemplate)
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

    private fun locatePropertyNames(messageTemplate: String): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        var index = 0
        while (true) {
            val start = messageTemplate.indexOf(PROPERTY_START, index)
            if (start == -1) break
            val end = messageTemplate.indexOf(PROPERTY_END, start + 1)
            if (end == -1) break
            val rawName = messageTemplate.substring(start + 1, end)
            if (rawName.isNotEmpty()) {
                val operator = when {
                    rawName.startsWith('$') -> "$"
                    rawName.startsWith('@') -> "@"
                    else -> ""
                }
                val name = if (operator.isNotEmpty()) rawName.substring(1) else rawName
                result.add(name to operator)
            }
            index = end + 1
        }
        return result
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
