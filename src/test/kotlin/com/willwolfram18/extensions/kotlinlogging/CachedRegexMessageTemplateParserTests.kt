package com.willwolfram18.extensions.kotlinlogging

class CachedRegexMessageTemplateParserTests : MessageTemplateParserTestBase() {
    override val parser: MessageTemplateParser = CachedRegexMessageTemplateParser()
}
