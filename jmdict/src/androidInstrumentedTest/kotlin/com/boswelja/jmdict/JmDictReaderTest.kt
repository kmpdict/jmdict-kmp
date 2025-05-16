package com.boswelja.jmdict

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JmDictReaderTest {

    @Test
    fun openJmDict() = runTest {
        val reader = JmDictReader(ApplicationProvider.getApplicationContext())
        reader.openJmDict()
    }
}
