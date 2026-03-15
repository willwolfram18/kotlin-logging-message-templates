package com.willwolfram18.extensions.kotlinlogging

interface MessageTemplateParser {
    fun parseTemplateArguments(messageTemplate: String, vararg args: Any?): Map<String, Any?>
}