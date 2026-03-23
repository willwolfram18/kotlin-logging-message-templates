package com.willwolfram18.extensions.kotlinlogging

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime, Mode.SampleTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 2)
open class MessageTemplateParserBenchmark {

    @Param("Regex", "CachedRegex", "StringIteration", "CachedStringIteration")
    var parserType: String = "Regex"

    private lateinit var parser: MessageTemplateParser

    @Setup
    fun setup() {
        parser = when (parserType) {
            "Regex" -> RegexMessageTemplateParser()
            "CachedRegex" -> CachedRegexMessageTemplateParser()
            "StringIteration" -> StringIterationTemplateParser()
            "CachedStringIteration" -> CachedStringIterationTemplateParser()
            else -> throw IllegalArgumentException("Unknown parser type: $parserType")
        }
    }

    @Benchmark
    fun benchmarkParseTemplateArguments3(): Map<String, Any?> {
        val template = "Argument {arg1}; Argument {arg2}; Argument {arg3}"
        return parser.parseTemplateArguments(template, 1, 2, 3)
    }

    @Benchmark
    fun benchmarkParseTemplateArguments5(): Map<String, Any?> {
        val template = "Argument {arg1}; Argument {arg2}; Argument {arg3}; Argument {arg4}; Argument {arg5}"
        return parser.parseTemplateArguments(template, 1, 2, 3, 4, 5)
    }

    @Benchmark
    fun benchmarkParseTemplateArguments1000(): Map<String, Any?> {
        val template = (1..1000).joinToString("; ") { "Argument {arg$it}" }
        return parser.parseTemplateArguments(template, *(1..1000).toList().toTypedArray())
    }
}
