package com.boswelja.jmdict

import android.content.Context
import java.util.zip.GZIPInputStream

actual class JmDictReader(private val context: Context) {
    actual suspend fun streamJmDict(): Sequence<Entry> {
        val reader = GZIPInputStream(context.resources.openRawResource(R.raw.jmdict)).bufferedReader()
        return reader.useLines { linesSequence ->
            linesSequence.asEntrySequence()
        }
    }
}
