package com.boswelja.jmdict

import io.github.boswelja.jmdict.jmdict.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.GZIPInputStream

actual suspend fun streamJmDict(): Sequence<Entry> {
    val compressedBytes = withContext(Dispatchers.IO) {
        Res.readBytes("files/jmdict.xml")
    }
    return GZIPInputStream(compressedBytes.inputStream()).bufferedReader()
        .lineSequence()
        .asEntrySequence()
}
