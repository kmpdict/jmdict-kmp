package com.boswelja.jmdict

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML

@OptIn(ExperimentalXmlUtilApi::class)
internal val Serializer = XML {
    defaultPolicy {
        pedantic = true
        autoPolymorphic = true
        throwOnRepeatedElement = true
        isStrictBoolean = true
        isStrictAttributeNames = true
        isXmlFloat = true
        verifyElementOrder = true
    }
}

expect suspend fun streamJmDict(): Sequence<Entry>

internal fun Sequence<String>.asEntrySequence(): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                Serializer.decodeFromString<JMdict>("<JMdict>${entryLines.flatten().joinToString(separator = "")}</JMdict>").entrys
            } else emptyList()
        }
}
