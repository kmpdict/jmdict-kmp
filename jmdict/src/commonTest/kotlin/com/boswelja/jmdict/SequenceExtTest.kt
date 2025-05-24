package com.boswelja.jmdict

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SequenceExtTest {

    @Test
    fun `chunkedUntil with empty sequence`() {
        val emptySequence = emptySequence<String>()
        val chunkedUntil = emptySequence.chunkedUntil { it == "Hello, world!" }
        assertTrue(chunkedUntil.none())
    }

    @Test
    fun `chunkedUntil where no elements match predicate`() {
        val targetSequence = sequenceOf(
            "Hello,",
            "World!",
            "I'm",
            "a",
            "sequence!"
        )
        val chunkedUntil = targetSequence.chunkedUntil { it.endsWith("~") } // Nothing in targetSequence ends with ~
        assertEquals(
            1,
            chunkedUntil.count()
        )
        assertEquals(
            targetSequence.toList(),
            chunkedUntil.first()
        )
    }

    @Test
    fun `chunkedUntil with one match`() {
        val targetSequence = sequenceOf(
            "Hello,",
            "World!",
            "I'm",
            "a",
            "sequence!"
        )
        val chunkedUntil = targetSequence.chunkedUntil { it == "I'm" }
        assertEquals(
            listOf(
                listOf(
                    "Hello,",
                    "World!",
                ),
                listOf(
                    "I'm",
                    "a",
                    "sequence!"
                )
            ),
            chunkedUntil.toList()
        )
    }
}
