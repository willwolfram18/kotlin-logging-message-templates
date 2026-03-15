package com.willwolfram18.extensions.kotlinlogging

import com.willwolfram18.extensions.kotlinlogging.*

class CachedRegexMessageTemplateParserTests : MessageTemplateParserTestBase() {
    override val parser: MessageTemplateParser = CachedRegexMessageTemplateParser()
}
