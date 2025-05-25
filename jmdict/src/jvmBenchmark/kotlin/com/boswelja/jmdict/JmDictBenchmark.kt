package com.boswelja.jmdict

import io.github.boswelja.jmdict.jmdict.generated.resources.Res
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.coroutines.runBlocking
import java.util.zip.GZIPInputStream

@State(Scope.Benchmark)
open class JmDictBenchmark {
    private lateinit var jmDictSequence: Sequence<String>

    @Setup
    fun prepare() {
        // Lets load the entire file into memory - we want to test the conversion and not IO performance here
        val compressedBytes = runBlocking { Res.readBytes("files/jmdict.xml") }
        val reader = GZIPInputStream(compressedBytes.inputStream()).bufferedReader()
        jmDictSequence = reader.readLines().asSequence()
        reader.close()
    }

    @Benchmark
    fun benchmarkDeserializeToEntries() {
        jmDictSequence.asEntrySequence().toList()
    }
}
