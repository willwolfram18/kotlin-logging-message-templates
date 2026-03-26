package com.willwolfram18.extensions.kotlinlogging

import io.github.oshai.kotlinlogging.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import io.mockk.*
import org.junit.jupiter.api.*

class KLoggerExtensionsTest {

    private val logger = spyk<KLogger>(KotlinLogging.logger { })
    private val payloadCapture = slot<Map<String, Any?>>()
    private val causeCapture = slot<Throwable?>()
    private val logEventBuilder = mockk<KLoggingEventBuilder>() {
        every { payload = capture(payloadCapture) } just Runs
        every { message = any() } just Runs
        every { cause = captureNullable(causeCapture) } just Runs
    }
    private val eventBuilderSlot = slot<KLoggingEventBuilder.() -> Unit>()
    private val mockParser = mockk<MessageTemplateParser> {
        every { parseTemplateArguments(any(), any()) } returns mapOf(
            "firstArg" to "arg"
        )
    }

    @BeforeEach
    fun configureParser() {
        parser = mockParser
    }

    @Test
    fun `debugTemplate without throwable should call atDebug with correct parameters`() {
        // Arrange: the logger to run on debug level
        every { logger.atDebug(capture(eventBuilderSlot)) } just Runs

        // Act
        logger.debugTemplate("test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atDebug(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBe null
        }
    }

    @Test
    fun `debugTemplate with throwable should call atDebug`() {
        // Arrange: the logger to run on debug level
        every { logger.atDebug(capture(eventBuilderSlot)) } just Runs

        // Arrange: the throwable
        val throwable = Exception("foobar")

        // Act
        logger.debugTemplate(throwable, "test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atDebug(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBeSameInstanceAs throwable
        }
    }

    @Test
    fun `traceTemplate without throwable should call atTrace with correct parameters`() {
        // Arrange: the logger to run on trace level
        every { logger.atTrace(capture(eventBuilderSlot)) } just Runs

        // Act
        logger.traceTemplate("test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atTrace(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBe null
        }
    }

    @Test
    fun `traceTemplate with throwable should call atTrace`() {
        // Arrange: the logger to run on trace level
        every { logger.atTrace(capture(eventBuilderSlot)) } just Runs

        // Arrange: the throwable
        val throwable = Exception("foobar")

        // Act
        logger.traceTemplate(throwable, "test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atTrace(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBeSameInstanceAs throwable
        }
    }

    @Test
    fun `warnTemplate without throwable should call atWarn with correct parameters`() {
        // Arrange: the logger to run on warn level
        every { logger.atWarn(capture(eventBuilderSlot)) } just Runs

        // Act
        logger.warnTemplate("test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atWarn(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBe null
        }
    }

    @Test
    fun `warnTemplate with throwable should call atWarn`() {
        // Arrange: the logger to run on warn level
        every { logger.atWarn(capture(eventBuilderSlot)) } just Runs

        // Arrange: the throwable
        val throwable = Exception("foobar")

        // Act
        logger.warnTemplate(throwable, "test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atWarn(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBeSameInstanceAs throwable
        }
    }

    @Test
    fun `errorTemplate without throwable should call atError with correct parameters`() {
        // Arrange: the logger to run on error level
        every { logger.atError(capture(eventBuilderSlot)) } just Runs

        // Act
        logger.errorTemplate("test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atError(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBe null
        }
    }

    @Test
    fun `errorTemplate with throwable should call atError`() {
        // Arrange: the logger to run on error level
        every { logger.atError(capture(eventBuilderSlot)) } just Runs

        // Arrange: the throwable
        val throwable = Exception("foobar")

        // Act
        logger.errorTemplate(throwable, "test {firstArg}", "arg")

        // Assert: the correct log method was called
        verify { logger.atError(any()) }

        // Assert: the delegate is set properly
        eventBuilderSlot.isCaptured shouldBe true

        // Assert: invoke the captured delegate and verify arguments are set
        eventBuilderSlot.captured.invoke(logEventBuilder)
        verify(exactly = 1) {
            logEventBuilder.message = "test {firstArg}"

            payloadCapture.captured shouldBe mapOf("firstArg" to "arg")

            causeCapture.captured shouldBeSameInstanceAs throwable
        }
    }
}
