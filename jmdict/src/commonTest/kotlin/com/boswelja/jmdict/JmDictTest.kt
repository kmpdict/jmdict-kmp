package com.boswelja.jmdict

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictTest {
    @Test
    fun `openJmDict successfully deserializes`() = runTest {
        openJmDict()
    }
}
