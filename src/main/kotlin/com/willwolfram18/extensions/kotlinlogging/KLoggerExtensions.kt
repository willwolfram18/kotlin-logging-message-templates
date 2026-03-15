package com.willwolfram18.extensions.kotlinlogging

import io.github.oshai.kotlinlogging.KLogger

var parser: MessageTemplateParser? = null
private fun parserInstance(): MessageTemplateParser = parser ?: RegexMessageTemplateParser()

fun KLogger.debugTemplate(messageTemplate: String, vararg args: Any) = atDebugTemplate(null, messageTemplate, *args)

fun KLogger.debugTemplate(throwable: Throwable, messageTemplate: String, vararg args: Any) = atDebugTemplate(throwable, messageTemplate, *args)

private fun KLogger.atDebugTemplate(throwable: Throwable?, messageTemplate: String, vararg args: Any) = atDebug {
    message = messageTemplate
    cause = throwable
    payload = parserInstance().parseTemplateArguments(messageTemplate, *args)
}

fun KLogger.infoTemplate(messageTemplate: String, vararg args: Any) = atInfoTemplate(null, messageTemplate, *args)

fun KLogger.infoTemplate(throwable: Throwable, messageTemplate: String, vararg args: Any) = atInfoTemplate(throwable, messageTemplate, *args)

private fun KLogger.atInfoTemplate(throwable: Throwable?, messageTemplate: String, vararg args: Any) = atInfo {
    message = messageTemplate
    cause = throwable
    payload = parserInstance().parseTemplateArguments(messageTemplate, args)
}
