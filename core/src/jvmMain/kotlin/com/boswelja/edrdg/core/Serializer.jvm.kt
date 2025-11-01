package com.boswelja.edrdg.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.source

internal actual suspend fun readCompressedBytes(): okio.Source {
    return withContext(Dispatchers.IO) {
        this.javaClass.getResourceAsStream("/dict.xml")!!.source()
    }
}
