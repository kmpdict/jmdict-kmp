package com.boswelja.jmdict

import com.squareup.zstd.okio.zstdDecompress
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import okio.BufferedSource
import okio.Source
import okio.buffer

@OptIn(ExperimentalXmlUtilApi::class)
internal val Serializer = XML {
    defaultPolicy {
        pedantic = false
        autoPolymorphic = true
        throwOnRepeatedElement = true
        isStrictBoolean = true
        isStrictAttributeNames = true
        isXmlFloat = true
        verifyElementOrder = true
    }
}

suspend fun streamJmDict(): Sequence<Entry> {
    val compressedSource = readCompressedBytes()
    return compressedSource
        .zstdDecompress()
        .buffer()
        .readLines()
        .asEntrySequence()
}

internal expect suspend fun readCompressedBytes(): Source

internal fun BufferedSource.readLines(): Sequence<String> {
    return sequence {
        while (!this@readLines.exhausted()) {
            yield(readUtf8Line()!!)
        }
    }
}

internal fun Sequence<String>.asEntrySequence(): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                Serializer.decodeFromString<JMdict>("<JMdict>${entryLines.flatten().joinToString(separator = "")}</JMdict>").entries
            } else emptyList()
        }
}
