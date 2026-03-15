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

        return locatePropertyNames(messageTemplate).zip(args.asSequence()).toMap()
    }

    private fun locatePropertyNames(messageTemplate: String): Sequence<String> = sequence {
        var index = 0
        while (true) {
            val start = messageTemplate.indexOf(PROPERTY_START, index)
            if (start == -1) break
            val end = messageTemplate.indexOf(PROPERTY_END, start + 1)
            if (end == -1) break
            val name = messageTemplate.substring(start + 1, end)
            if (name.isNotEmpty()) {
                yield(name)
            }
            index = end + 1
        }
    }
}