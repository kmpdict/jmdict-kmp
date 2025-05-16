package com.boswelja.jmdict

import jmdict_kmp.jmdict.generated.resources.Res
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.core.KtXmlReader
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.ByteArrayInputStream

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

@OptIn(ExperimentalResourceApi::class, ExperimentalXmlUtilApi::class)
suspend fun openJmDict(): JMdict {
    val input = ByteArrayInputStream(Res.readBytes("files/jmdict.xml")).buffered()
    return Serializer.decodeFromReader(KtXmlReader(input))
}
