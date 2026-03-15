package com.willwolfram18.extensions.kotlinlogging

import com.fasterxml.jackson.annotation.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.maps.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import kotlin.time.*

abstract class MessageTemplateParserTestBase {
    abstract val parser: MessageTemplateParser

    data class TestDataClass(
        @field:JsonProperty("custom_field")
        val myProperty: String,
        val normalField: Int
    )

    class TestNormalClass(
        @field:JsonProperty("renamed_prop")
        val someProp: String,
        val anotherProp: Double
    )

    data class OuterClass(
        val name: String,
        val inner: InnerClass
    )

    data class InnerClass(
        val value: Int
    )

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
    fun `GIVEN opening curly without closing curly WHEN parsing THEN unclosed property is excluded`(
        messageTemplate: String,
        expectedPairs: String
    ) {
        // Act
        val result = parser.parseTemplateArguments(messageTemplate, 1, 2, 3)

        // Act: build the expected map from our delimited string
        val parsedPairs = when {
            expectedPairs.isEmpty() -> emptyList()
            else -> expectedPairs.split(";").map {
                val (key, value) = it.split(",")
                key to (value.toInt() as Any?)
            }
        }.toMap()

        // Assert: verify the resulting property names
        result shouldHaveSize parsedPairs.size
        result shouldBe parsedPairs
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "I am a message missing an open curly}|''",
            "I have {oneProp} and another bad prop}|oneProp,1",
            "I have {oneProp} and {twoProp} but not three}|oneProp,1;twoProp,2"
        ],
        delimiter = '|',
    )
    fun `GIVEN closing curly without an opening WHEN parsing THEN unopened property is excluded`(
        messageTemplate: String,
        expectedPairs: String
    ) {
        // Act
        val result = parser.parseTemplateArguments(messageTemplate, 1, 2, 3)

        // Act: build the expected map from our delimited string
        val parsedPairs = when {
            expectedPairs.isEmpty() -> emptyList()
            else -> expectedPairs.split(";").map {
                val (key, value) = it.split(",")
                key to (value.toInt() as Any?)
            }
        }.toMap()

        // Assert: verify the resulting property names
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
        val iterations = 20
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

    @Test
    fun `GIVEN stringify formatter with integer WHEN parsing THEN value is converted to string`() {
        // Arrange
        val template = "Value is {\$num}"

        // Act
        val result = parser.parseTemplateArguments(template, 42)

        // Assert
        result shouldHaveSize 1
        result shouldContain ("num" to "42")
    }

    @ParameterizedTest
    @ValueSource(
        booleans = [true, false]
    )
    fun `GIVEN stringify formatter with boolean WHEN parsing THEN value is converted to string`(
        value: Boolean
    ) {
        // Arrange
        val template = "Flag is {\$flag}"

        // Act
        val result = parser.parseTemplateArguments(template, value)

        // Assert
        result shouldHaveSize 1
        result shouldContain ("flag" to value.toString())
    }

    @Test
    fun `GIVEN stringify formatter with list WHEN parsing THEN value is converted to string`() {
        // Arrange
        val template = "List is {\$items}"
        val list = listOf(1, 2, 3)

        // Act
        val result = parser.parseTemplateArguments(template, list)

        // Assert
        result shouldHaveSize 1
        result shouldContain ("items" to "[1, 2, 3]")
    }

    @Test
    fun `GIVEN stringify formatter with map WHEN parsing THEN value is converted to string`() {
        // Arrange
        val template = "Map is {\$data}"
        val map = mapOf(1 to "hello", 2 to "world")

        // Act
        val result = parser.parseTemplateArguments(template, map)

        // Assert
        result shouldHaveSize 1
        result shouldContain ("data" to "{1=hello, 2=world}")
    }

    @Test
    fun `GIVEN destructure formatter with data class WHEN parsing THEN value is converted to map with JSON property names`() {
        // Arrange
        val obj = TestDataClass("test_value", 123)
        val template = "Object is {@obj}"

        // Act
        val result = parser.parseTemplateArguments(template, obj)

        // Assert
        result shouldHaveSize 1
        val objProperty = result["obj"]
        require(objProperty is Map<*, *>) { "Expected Map" }
        val objectMap = objProperty as Map<String, Any?>
        objectMap shouldContain ("custom_field" to "test_value")
        objectMap shouldContain ("normalField" to 123)
    }

    @Test
    fun `GIVEN destructure formatter with normal class WHEN parsing THEN value is converted to map with JSON property names`() {
        // Arrange
        val obj = TestNormalClass("some_value", 45.67)
        val template = "Normal object is {@normalObj}"

        // Act
        val result = parser.parseTemplateArguments(template, obj)

        // Assert
        result shouldHaveSize 1
        val objProperty = result["normalObj"]
        require(objProperty is Map<*, *>) { "Expected Map" }
        val objectMap = objProperty as Map<String, Any?>
        objectMap shouldContain ("renamed_prop" to "some_value")
        objectMap shouldContain ("anotherProp" to 45.67)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `GIVEN destructure formatter with scalar values WHEN parsing THEN values are preserved unchanged`(flag: Boolean) {
        // Arrange
        val template = "String {@str}, int {@num}, bool {@flag}, float {@pi}"

        // Act
        val result = parser.parseTemplateArguments(template, "hello", 42, flag, 3.14)

        // Assert
        result shouldHaveSize 4
        result shouldContain ("str" to "hello")
        result shouldContain ("num" to 42)
        result shouldContain ("flag" to flag)
        result shouldContain ("pi" to 3.14)
    }

    @Test
    fun `GIVEN destructure formatter with integer iterables WHEN parsing THEN values are converted to lists`() {
        // Arrange
        val list = listOf(1, 2, 3)
        val array = arrayOf(4, 5, 6)
        val seq = sequenceOf(7, 8, 9)
        val template = "List {@list}, array {@array}, seq {@seq}"

        // Act
        val result = parser.parseTemplateArguments(template, list, array, seq)

        // Assert
        result shouldHaveSize 3

        val listValue = result["list"]
        require(listValue is List<*>) { "Expected List" }
        listValue shouldContainExactly listOf<Any?>(1, 2, 3)

        val arrayValue = result["array"]
        require(arrayValue is List<*>) { "Expected List" }
        arrayValue shouldContainExactly listOf<Any?>(4, 5, 6)

        val seqValue = result["seq"]
        require(seqValue is List<*>) { "Expected List" }
        seqValue shouldContainExactly listOf<Any?>(7, 8, 9)
    }

    @Test
    fun `GIVEN destructure formatter with mixed iterables WHEN parsing THEN values are converted to lists`() {
        // Arrange
        val list = listOf("foo", 1)
        val array = arrayOf<Any>(2, "bar")
        val seq = sequenceOf("baz", 3)
        val template = "Mixed list {@list}, mixed array {@array}, mixed seq {@seq}"

        // Act
        val result = parser.parseTemplateArguments(template, list, array, seq)

        // Assert
        result shouldHaveSize 3

        val listValue = result["list"]
        require(listValue is List<*>) { "Expected List" }
        listValue shouldContainExactly listOf<Any?>("foo", 1)

        val arrayValue = result["array"]
        require(arrayValue is List<*>) { "Expected List" }
        arrayValue shouldContainExactly listOf(2, "bar")

        val seqValue = result["seq"]
        require(seqValue is List<*>) { "Expected List" }
        seqValue shouldContainExactly listOf("baz", 3)
    }

    @Test
    fun `GIVEN destructure formatter with nested class WHEN parsing THEN nested values are converted to maps`() {
        // Arrange
        val obj = OuterClass("outer_value", InnerClass(99))
        val template = "Outer object is {@outerObj}"

        // Act
        val result = parser.parseTemplateArguments(template, obj)

        // Assert
        result shouldHaveSize 1
        val objProperty = result["outerObj"]
        require(objProperty is Map<*, *>) { "Expected Map" }
        @Suppress("UNCHECKED_CAST")
        val objectMap = objProperty as Map<String, Any?>
        objectMap shouldContain ("name" to "outer_value")

        val innerProperty = objectMap["inner"]
        require(innerProperty is Map<*, *>) { "Expected Map" }
        @Suppress("UNCHECKED_CAST")
        val innerMap = innerProperty as Map<String, Any?>
        innerMap shouldContain ("value" to 99)
    }
}
