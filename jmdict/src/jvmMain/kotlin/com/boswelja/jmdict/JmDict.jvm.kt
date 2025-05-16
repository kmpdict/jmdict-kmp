package com.boswelja.jmdict

import nl.adaptivity.xmlutil.xmlStreaming
import java.util.zip.GZIPInputStream

actual class JmDictReader {
    actual suspend fun openJmDict(): JMdict {
        val stream = GZIPInputStream(object{}::class.java.getResourceAsStream("jmdict.xml"))
        return Serializer.decodeFromReader(xmlStreaming.newReader(stream.bufferedReader()))
    }
}
