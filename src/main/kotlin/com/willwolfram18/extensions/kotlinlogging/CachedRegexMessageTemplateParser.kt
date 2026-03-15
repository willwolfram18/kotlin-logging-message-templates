package com.willwolfram18.extensions.kotlinlogging

class CachedRegexMessageTemplateParser : MessageTemplateParser {
    companion object {
        private val fieldNameRegex = """\{(?<name>[a-zA-Z0-9]+)}""".toRegex()
    }

    private val cache: MutableMap<String, List<String>> = mutableMapOf()

    override fun parseTemplateArguments(
        messageTemplate: String,
        vararg args: Any?
    ): Map<String, Any?> {
        val names = cache.getOrPut(messageTemplate) {
            fieldNameRegex.findAll(messageTemplate).map { it.groupValues[1] }.toList()
        }
        return names.zip(args.asIterable()).toMap()
    }
}
