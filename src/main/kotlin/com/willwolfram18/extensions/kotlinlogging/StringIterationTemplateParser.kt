package com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging

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

        TODO()
    }

//    private fun locatePropertyNames(messageTemplate: String): Sequence<String> = sequence {
//        val firstOpening =
//    }
}