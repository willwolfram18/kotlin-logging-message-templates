package com.willwolfram18.extensions.kotlinlogging

class StringIterationTemplateParser : MessageTemplateParser {
    companion object {
        const val PROPERTY_START = '{'
        const val PROPERTY_END = '}'
    }

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        if (messageTemplate.isEmpty()) {
            return emptyMap()
        }

        return locatePropertyNames(messageTemplate).zip(args.asSequence()).associate { (nameAndStringify, value) ->
            val (name, isStringify) = nameAndStringify
            name to if (isStringify) value?.toString() else value
        }
    }

    private fun locatePropertyNames(messageTemplate: String): Sequence<Pair<String, Boolean>> = sequence {
        var index = 0
        while (true) {
            val start = messageTemplate.indexOf(PROPERTY_START, index)
            if (start == -1) break
            val end = messageTemplate.indexOf(PROPERTY_END, start + 1)
            if (end == -1) break
            val rawName = messageTemplate.substring(start + 1, end)
            if (rawName.isNotEmpty()) {
                val isStringify = rawName.startsWith('$')
                val name = if (isStringify) rawName.substring(1) else rawName
                yield(name to isStringify)
            }
            index = end + 1
        }
    }
}