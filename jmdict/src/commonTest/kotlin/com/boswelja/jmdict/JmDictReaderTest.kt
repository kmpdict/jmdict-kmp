package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JmDictReaderTest {

    @Test
    fun streamJmDict_streamsAllEntries() = runTest {
        var entryCount = 0
        streamJmDict().forEach { _ -> entryCount++ }
        assertEquals(
            Metadata.entryCount,
            entryCount
        )
    }

    @Test
    fun streamJmDict_hasNonEnglishEntries() = runTest {
        val hasNonEnglish = streamJmDict().any { entry ->
            entry.senses.any { sense -> sense.lsources.any { it.lang != "eng" } }
        }
        assertTrue(hasNonEnglish)
    }
}
