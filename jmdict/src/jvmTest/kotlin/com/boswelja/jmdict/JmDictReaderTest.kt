package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
    fun streamJmDict_containsNonEngEntries() = runTest {
        var entryCount = 0
        streamJmDict().forEach { entry ->
            if (entry.senses.any { it.glosses.any { it.lang != "eng" }}) entryCount++
        }
        assertNotEquals(
            0,
            entryCount
        )
    }
}
