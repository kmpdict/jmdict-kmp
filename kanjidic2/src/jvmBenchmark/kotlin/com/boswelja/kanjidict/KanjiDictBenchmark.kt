package com.boswelja.kanjidict

import com.squareup.zstd.okio.zstdDecompress
import io.github.boswelja.kanjidic2.kanjidic2.generated.resources.Res
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.buffer

@State(Scope.Benchmark)
open class KanjiDictBenchmark {
    private lateinit var jmDictSequence: Sequence<String>

    @Setup
    fun prepare() {
        // Lets load the entire file into memory - we want to test the conversion and not IO performance here
        val compressedBytes = runBlocking { Res.readBytes("files/kanjidict.xml") }
        val buffer = Buffer()
        buffer.write(compressedBytes)
        jmDictSequence = buffer
            .zstdDecompress()
            .buffer()
            .readLines()
        buffer.close()
    }

    @Benchmark
    fun benchmarkDeserializeToEntries() {
        jmDictSequence.asCharacterSequence().toList()
    }
}
