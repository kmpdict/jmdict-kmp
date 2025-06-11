package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    //@Ignore("This is a placeholder test that we can use to verify simply opening the stream.")
    fun streamJmDictTest() = runTest {
        var entryCount = 0
        streamJmDict()
            .forEach { entryCount++ }
        println(entryCount)
    }
}
