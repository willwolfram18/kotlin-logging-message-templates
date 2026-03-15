package com.willwolfram18.extensions.kotlinlogging

class RegexMessageTemplateParser : MessageTemplateParser {
    companion object {
        private val fieldNameRegex = """\{(\$?)(?<name>[a-zA-Z0-9]+)}""".toRegex()
    }

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        val namedArgumentMatches = fieldNameRegex.findAll(messageTemplate)
        val namedArgumentPairs = namedArgumentMatches.zip(args.asSequence())

        return buildMap {
            for ((match, value) in namedArgumentPairs) {
                val isStringify = match.groupValues[1] == "$"
                val name = match.groupValues[2]
                val finalValue = if (isStringify) value?.toString() else value
                put(name, finalValue)
            }
        }
    }
}