package com.boswelja.jmdict.dtd

import kotlinx.io.Buffer
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SourceExtTest {
    @Test
    fun `readLinesUntil returns correct result for zero lines`() {
        val source = Buffer()
        val result = source.readLinesUntil { false }
        assertNull(result)
    }

    @Test
    fun `readLinesUntil returns correct result for one line`() {
        val conditions = mapOf(
            "Hello, world!" to "!"
        )
        conditions.forEach { (line, containsPredicate) ->
            val source = Buffer()
            source.writeString(line)
            val result = source.readLinesUntil {
                it.contains(containsPredicate)
            }
            assertEquals(
                line,
                result,
                "Expected '$line', but got '$result'"
            )
        }
        val source = Buffer()
        val result = source.readLinesUntil { false }
        assertNull(result)
    }

    @Test
    fun `readLinesUntil returns correct result for multiple lines`() {
        val conditions = mapOf(
            "Hello, world!\nThis is a test.\nEnd of line~" to "Hello, world! This is a test. End of line~",
            "Line one\nLine two\nLine three~" to "Line one Line two Line three~",
            "Line two\n\nLine four\n\nLine six~" to "Line two Line four Line six~",
            "Indented line\n    Indented line two\n      Indented line three~" to "Indented line Indented line two Indented line three~"
        )
        conditions.forEach { (line, expected) ->
            val source = Buffer()
            source.writeString(line)
            val result = source.readLinesUntil {
                it.endsWith("~")
            }
            assertEquals(
                expected,
                result,
                "Expected '$expected', but got '$result'"
            )
        }
    }
}
