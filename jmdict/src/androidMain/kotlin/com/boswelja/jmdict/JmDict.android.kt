package com.boswelja.jmdict

import android.content.Context
import kotlinx.serialization.decodeFromString
import java.util.zip.GZIPInputStream

actual class JmDictReader(private val context: Context) {
    actual suspend fun streamJmDict(): Sequence<Entry> {
        val stream = GZIPInputStream(context.resources.openRawResource(R.raw.jmdict))
        val reader = stream.bufferedReader()
        return reader.lineSequence()
            .dropWhile { !it.contains("<entry>") }
            .chunkedUntil { it.contains("<entry>") }
            .mapNotNull { entryLines ->
                if (entryLines.isNotEmpty()) {
                    Serializer.decodeFromString(entryLines.joinToString(separator = ""))
                } else {
                    null
                }
            }
    }
}
