package com.boswelja.edrdg.core

import com.squareup.zstd.okio.zstdDecompress
import nl.adaptivity.xmlutil.serialization.XML
import okio.Source
import okio.buffer

public val Serializer: XML = XML {
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

internal expect suspend fun readCompressedBytes(): Source

public suspend fun streamDict(): Sequence<String> {
    val compressedSource = readCompressedBytes()
    return compressedSource
        .zstdDecompress()
        .buffer()
        .readLines()
}