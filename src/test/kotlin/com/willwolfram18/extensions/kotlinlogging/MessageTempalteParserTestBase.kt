package com.willwolfram18.extensions.kotlinlogging

import com.willwolfram18.extensions.kotlinlogging.com.willwolfram18.extensions.kotlinlogging.MessageTemplateParser
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

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

    @ParameterizedTest
    @CsvSource(
        value = [
            "I am a message with a malformed {property|''",
            "I have {oneProp} and another bad {prop|oneProp,1",
            "I have {oneProp} and {twoProp} but not {three|oneProp,1;twoProp,2"
        ],
        delimiter = '|'
    )
    fun `GIVEN opening curly without closing curly WHEN parsing THEN empty map is returned`(
        messageTemplate: String,
        expectedPairs: String
    ) {
        // Act
        val result = parser.parseTemplateArguments(messageTemplate, 1, 2, 3)

        // Assert
        val parsedPairs = when {
            expectedPairs.isEmpty() -> emptyList()
            else -> expectedPairs.split(";").map {
                val (key, value) = it.split(",")
                key to (value as Any?)
            }
        }.groupBy { it.first }

        result shouldHaveSize parsedPairs.size
        result shouldBe parsedPairs
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
    @Tag("long-running")
    @ValueSource(
        ints = [
            1,
            3,
            5,
            10,
            // Probably unrealistic scenarios but worth trying it out to see
            100,
            1000,
            10_000,
            // Definitely unrealistic
            1_000_000
        ]
    )
    fun performanceBenchmark(argumentCount: Int) {
        // Arrange: the message template
        val messageTemplate = (1..argumentCount).joinToString("; ") {
            "Argument {arg$it}"
        }

        // Arrange: the values
        val values = (1..argumentCount).toList()

        // Run in a loop to
        val durations = mutableListOf<Long>()
        val iterations = 100
        repeat(iterations) {
            // Arrange: timer
            val timer = TimeSource.Monotonic.markNow()

            // Act
            val result = parser.parseTemplateArguments(messageTemplate, *values.toTypedArray())
            durations.add(timer.elapsedNow().inWholeNanoseconds)

            // Assert
            result shouldHaveSize argumentCount
        }

        val avgDuration = (durations.sum() / durations.size).toDuration(DurationUnit.NANOSECONDS)
        val maxDuration = durations.max().toDuration(DurationUnit.NANOSECONDS)
        val minDuration = durations.min().toDuration(DurationUnit.NANOSECONDS)

        println("Over $iterations iterations, min duration=$minDuration, max duration=$maxDuration, avg duration=$avgDuration")
    }
}