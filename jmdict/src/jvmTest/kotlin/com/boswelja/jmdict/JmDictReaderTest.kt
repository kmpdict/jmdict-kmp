package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    fun streamJmDict() = runTest {
        val reader = JmDictReader()
        reader.streamJmDict()
    }
}
