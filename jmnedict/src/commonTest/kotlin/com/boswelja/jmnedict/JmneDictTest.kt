package com.boswelja.jmnedict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JmneDictTest {

    @Test
    fun streamJmDict_streamsAllEntries() = runTest {
        var entryCount = 0
        streamJmmeDict().forEach { _ -> entryCount++ }
        assertEquals(
            Metadata.entryCount,
            entryCount
        )
    }
}
