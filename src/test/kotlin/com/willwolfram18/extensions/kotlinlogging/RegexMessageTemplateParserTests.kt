package com.willwolfram18.extensions.kotlinlogging

import com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging.MessageTemplateParser
import com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging.RegexMessageTemplateParser

class RegexMessageTemplateParserTests : MessageTempalteParserTestBase() {
    override val parser: MessageTemplateParser = RegexMessageTemplateParser()
}