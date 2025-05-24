package com.boswelja.jmdict

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    @Ignore("This is a placeholder test that we can use to verify simply opening the stream.")
    fun streamJmDict() = runTest {
        val reader = JmDictReader(ApplicationProvider.getApplicationContext())
        var entryCount = 0
        reader.streamJmDict()
            .forEach { entryCount++ }
        println(entryCount)
    }
}
