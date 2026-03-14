package com.willwolfram18.extensions.kotlinlogging

import com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging.MessageTemplateParser
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.TimeSource

abstract class MessageTempalteParserTestBase {
    abstract val parser: MessageTemplateParser

    @Test
    fun `GIVEN no named properties in template WHEN parsing THEN empty map is returned`() {
        // Arrange
        val template = "I am a message template without arguments"

        // Act
        val result = parser.parseTemplateArguments(template, "First arg", 2, "Another")

        // Assert
        result.shouldBeEmpty()
    }

    @Test
    fun `GIVEN named property count in template matches values WHEN parsing THEN map contains all properties`() {
        // Arrange
        val template = "My name is {name} and I am {years} old with {balance} in my bank account"

        // Act
        val result = parser.parseTemplateArguments(template, "Jimothy", 32, 101.43)

        // Assert
        result shouldHaveSize 3
        result shouldContain ("name" to "Jimothy")
        result shouldContain ("years" to 32)
        result shouldContain ("balance" to 101.43)
    }

    @Test
    fun `GIVEN more named properties than values WHEN parsing THEN excess named properties are omitted`() {
        // Arrange
        val template = "Property {numOne} and property {numTwo} and property {numThree}"

        // Act
        val result = parser.parseTemplateArguments(template, "One")

        // Assert
        result shouldHaveSize 1
        result shouldContain ("numOne" to "One")
        result.keys shouldNotContainAnyOf listOf("numTwo", "numThree")
    }

    @Test
    fun `GIVEN fewer named properties than values WHEN parsing THEN excess values are omitted`() {
        // Arrange
        val template = "Property {numOne} and that's it"

        // Act
        val result = parser.parseTemplateArguments(template, "One", 2, "three")

        // Assert
        result shouldHaveSize 1
        result shouldContain ("numOne" to "One")
        result.values shouldNotContainAnyOf listOf(2, "three")
    }

    @ParameterizedTest
    @ValueSource(
        ints = [
            1,
            3,
            5,
            10,
            // Probably unrealistic scenarios but worth trying it out to see
            100,
            1000,
            10000
        ]
    )
    fun performanceBenchmark(argumentCount: Int) {
        // Arrange: the message template
        val messageTemplate = (1..argumentCount).joinToString("; ") {
            "Argument {arg$it}"
        }

        // Arrange: the values
        val values = (1..argumentCount).toList()

        // Arrange: timer
        val timer = TimeSource.Monotonic.markNow()

        // Act
        val result = parser.parseTemplateArguments(messageTemplate, *values.toTypedArray())
        val elapsed = timer.elapsedNow()

        println("Execution with $argumentCount took $elapsed")

        // Assert
        result shouldHaveSize argumentCount
    }
}