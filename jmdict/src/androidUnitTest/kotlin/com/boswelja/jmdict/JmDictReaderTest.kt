package com.boswelja.jmdict

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Ignore("No zstd for these tests")
@RunWith(AndroidJUnit4::class)
class JmDictReaderTest {

    @BeforeTest
    fun setUp() {
        val resources = InstrumentationRegistry.getInstrumentation().context.resources
        ResourcesInitializer.resources = resources
    }

    @Test
    fun streamJmDict_streamsAllEntries() = runTest {
        var entryCount = 0
        streamJmDict().forEach { _ -> entryCount++ }
        assertEquals(
            Metadata.entryCount,
            entryCount
        )
    }

    @Test
    fun streamJmDict_hasNonEnglishEntries() = runTest {
        val hasNonEnglish = streamJmDict().any { entry ->
            entry.senses.any { sense ->
                sense.glosses.any { it.lang != "eng" }
            }
        }
        assertTrue(hasNonEnglish)
    }
}
