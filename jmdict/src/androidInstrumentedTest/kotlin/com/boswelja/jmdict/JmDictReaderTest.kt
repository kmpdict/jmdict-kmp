package com.boswelja.jmdict

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    fun streamJmDict() = runTest {
        val reader = JmDictReader(ApplicationProvider.getApplicationContext())
        var entryCount = 0
        reader.streamJmDict()
            .forEach { entryCount++ }
        println(entryCount)
    }
}
