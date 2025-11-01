package com.boswelja.kanjidict

import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement

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

suspend fun streamKanjiDict(): Sequence<Character> {
    return streamDict()
        .asCharacterSequence()
}

internal fun Sequence<String>.asCharacterSequence(): Sequence<Character> {
    return this
        .dropWhile { !it.contains("<character>") }
        .chunkedUntil { it.contains("<character>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                Serializer.decodeFromString<KanjiDictCharacters>("<kanjidic2>${entryLines.flatten().joinToString(separator = "")}</kanjidic2>").characters
            } else emptyList()
        }
}

@Serializable
@XmlElement(value = true)
@SerialName(value = "kanjidic2")
internal class KanjiDictCharacters(
    @XmlElement(value = true)
    @SerialName(value = "character")
    public val characters: List<Character>,
)