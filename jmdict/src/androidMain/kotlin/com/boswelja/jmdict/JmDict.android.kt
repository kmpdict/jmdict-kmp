package com.boswelja.jmdict

import android.content.Context
import nl.adaptivity.xmlutil.xmlStreaming

actual class JmDictReader(private val context: Context) {
    actual suspend fun openJmDict(): JMdict {
        val stream = context.resources.openRawResource(R.raw.jmdict)
        return Serializer.decodeFromReader(xmlStreaming.newReader(stream.bufferedReader()))
    }
}
