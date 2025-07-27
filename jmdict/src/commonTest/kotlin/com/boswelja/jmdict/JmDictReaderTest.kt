package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
