package com.boswelja.jmdict

import jmdict_kmp.jmdict.generated.resources.Res
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlBufferedReader
import nl.adaptivity.xmlutil.core.KtXmlReader
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.ByteArrayInputStream

@OptIn(ExperimentalResourceApi::class, ExperimentalXmlUtilApi::class)
suspend fun openJmDict(): JMdict {
    val input = ByteArrayInputStream(Res.readBytes("files/jmdict.xml"))
    val bufferedReader = XmlBufferedReader(KtXmlReader(input))
    return XML.decodeFromReader(bufferedReader)
}
