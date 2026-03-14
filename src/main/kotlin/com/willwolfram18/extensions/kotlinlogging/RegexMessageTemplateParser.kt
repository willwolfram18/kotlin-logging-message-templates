package com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging

class RegexMessageTemplateParser : MessageTemplateParser {
    companion object {
        private val fieldNameRegex = """\{(?<name>[a-zA-Z0-9]+)}""".toRegex()
    }

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        val namedArgumentMatches = fieldNameRegex.findAll(messageTemplate)
        val namedArgumentPairs = namedArgumentMatches.zip(args.asSequence())

        return buildMap {
            for ((name, value) in namedArgumentPairs) {
                // Group values matches "{name}" first, THEN "name"
                put(name.groupValues[1], value)
            }
        }
    }
}