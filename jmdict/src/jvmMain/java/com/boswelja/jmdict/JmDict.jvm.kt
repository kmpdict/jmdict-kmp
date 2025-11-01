package com.boswelja.jmdict

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Source
import okio.source

internal actual suspend fun readCompressedBytes(): Source {
    return withContext(Dispatchers.IO) {
        this.javaClass.getResourceAsStream("/dict.xml")!!.source()
    }
}
