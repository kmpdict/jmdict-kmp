package com.boswelja.jmdict

import kotlinx.serialization.decodeFromString
import java.util.zip.GZIPInputStream

actual class JmDictReader {
    actual suspend fun streamJmDict(): Sequence<Entry> {
        val reader = GZIPInputStream(object{}::class.java.getResourceAsStream("jmdict.xml")).bufferedReader()
        return reader.useLines { linesSequence ->
            linesSequence
                .dropWhile { !it.contains("<entry>") }
                .chunkedUntil { it.contains("<entry>") }
                .map { entryLines ->
                    Serializer.decodeFromString(entryLines.joinToString(separator = ""))
                }
        }
    }
}
