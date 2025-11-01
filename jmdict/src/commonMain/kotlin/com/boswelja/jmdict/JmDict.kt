package com.boswelja.jmdict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.decodeFromString

suspend fun streamJmDict(): Sequence<Entry> {
    return streamDict()
        .asEntrySequence()
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
