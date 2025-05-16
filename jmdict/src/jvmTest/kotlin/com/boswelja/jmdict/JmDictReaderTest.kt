package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    fun openJmDict() = runTest {
        val reader = JmDictReader()
        reader.openJmDict()
    }
}
