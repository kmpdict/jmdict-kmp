package com.boswelja.kanjidict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KanjiDictReaderTest {

    @Test
    fun streamJmDict_streamsAllEntries() = runTest {
        var entryCount = 0
        streamKanjiDict().forEach { _ -> entryCount++ }
        assertEquals(
            Metadata.entryCount,
            entryCount
        )
    }
}
