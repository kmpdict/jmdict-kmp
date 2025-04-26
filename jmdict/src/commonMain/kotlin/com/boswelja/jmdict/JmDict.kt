package com.boswelja.jmdict

import jmdict_kmp.jmdict.generated.resources.Res
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
suspend fun openJmDict(): JMdict {
    val input = Res.readBytes("files/jmdict.xml")
    return XML.decodeFromString(input.decodeToString())
}
