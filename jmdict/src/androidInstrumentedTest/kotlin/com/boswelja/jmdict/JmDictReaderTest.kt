package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    fun streamJmDictTest() = runTest {
        var entryCount = 0
        streamJmDict()
            .forEach { entryCount++ }
        println(entryCount)
    }
}
