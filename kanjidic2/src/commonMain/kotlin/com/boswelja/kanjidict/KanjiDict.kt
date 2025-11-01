package com.boswelja.kanjidict

import com.squareup.zstd.okio.zstdDecompress
import io.github.boswelja.kanjidic2.kanjidic2.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import okio.Buffer
import okio.BufferedSource
import okio.buffer

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
    val compressedBytes = withContext(Dispatchers.IO) {
        Res.readBytes("files/kanjidict.xml")
    }
    val buffer = Buffer()
    buffer.write(compressedBytes)
    return buffer
        .zstdDecompress()
        .buffer()
        .readLines()
        .asCharacterSequence()
}

internal fun BufferedSource.readLines(): Sequence<String> {
    return sequence {
        while (!this@readLines.exhausted()) {
            yield(readUtf8Line()!!)
        }
    }
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