package com.boswelja.jmdict

import nl.adaptivity.xmlutil.xmlStreaming

actual class JmDictReader {
    actual suspend fun openJmDict(): JMdict {
        val stream = object{}::class.java.getResourceAsStream("jmdict.xml")
        return Serializer.decodeFromReader(xmlStreaming.newReader(stream.bufferedReader()))
    }
}
