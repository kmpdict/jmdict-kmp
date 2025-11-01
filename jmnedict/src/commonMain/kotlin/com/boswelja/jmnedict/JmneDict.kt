package com.boswelja.jmnedict

import com.squareup.zstd.okio.zstdDecompress
import io.github.boswelja.jmnedict.jmnedict.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import okio.Buffer
import okio.BufferedSource
import okio.buffer
import kotlin.collections.emptyList

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

suspend fun streamJmmeDict(): Sequence<Entry> {
    val compressedBytes = withContext(Dispatchers.IO) {
        Res.readBytes("files/jmnedict.xml")
    }
    val buffer = Buffer()
    buffer.write(compressedBytes)
    return buffer
        .zstdDecompress()
        .buffer()
        .readLines()
        .asEntrySequence()
}

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
                Serializer.decodeFromString<JMnedict>("<JMnedict>${entryLines.flatten().joinToString(separator = "")}</JMnedict>").entries
            } else emptyList()
        }
}
