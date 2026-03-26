package com.willwolfram18.extensions.kotlinlogging

import io.github.oshai.kotlinlogging.KLogger

var parser: MessageTemplateParser? = null
private fun parserInstance(): MessageTemplateParser = parser ?: StringIterationTemplateParser()

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
    payload = parserInstance().parseTemplateArguments(messageTemplate, *args)
}

fun KLogger.traceTemplate(messageTemplate: String, vararg args: Any) = atTraceTemplate(null, messageTemplate, *args)

fun KLogger.traceTemplate(throwable: Throwable, messageTemplate: String, vararg args: Any) = atTraceTemplate(throwable, messageTemplate, *args)

private fun KLogger.atTraceTemplate(throwable: Throwable?, messageTemplate: String, vararg args: Any) = atTrace {
    message = messageTemplate
    cause = throwable
    payload = parserInstance().parseTemplateArguments(messageTemplate, *args)
}

fun KLogger.warnTemplate(messageTemplate: String, vararg args: Any) = atWarnTemplate(null, messageTemplate, *args)

fun KLogger.warnTemplate(throwable: Throwable, messageTemplate: String, vararg args: Any) = atWarnTemplate(throwable, messageTemplate, *args)

private fun KLogger.atWarnTemplate(throwable: Throwable?, messageTemplate: String, vararg args: Any) = atWarn {
    message = messageTemplate
    cause = throwable
    payload = parserInstance().parseTemplateArguments(messageTemplate, *args)
}

fun KLogger.errorTemplate(messageTemplate: String, vararg args: Any) = atErrorTemplate(null, messageTemplate, *args)

fun KLogger.errorTemplate(throwable: Throwable, messageTemplate: String, vararg args: Any) = atErrorTemplate(throwable, messageTemplate, *args)

private fun KLogger.atErrorTemplate(throwable: Throwable?, messageTemplate: String, vararg args: Any) = atError {
    message = messageTemplate
    cause = throwable
    payload = parserInstance().parseTemplateArguments(messageTemplate, *args)
}
