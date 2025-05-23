package com.boswelja.jmdict

import java.util.zip.GZIPInputStream

actual class JmDictReader {
    actual suspend fun streamJmDict(): Sequence<Entry> {
        val reader = GZIPInputStream(object{}::class.java.getResourceAsStream("jmdict.xml")).bufferedReader()
        return reader.useLines { linesSequence ->
            linesSequence.asEntrySequence()
        }
    }
}
