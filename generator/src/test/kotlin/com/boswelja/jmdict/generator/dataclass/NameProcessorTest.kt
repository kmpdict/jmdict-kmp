package com.boswelja.jmdict.generator.dataclass

import kotlin.test.Test
import kotlin.test.assertEquals

class NameProcessorTest {
    @Test
    fun `toPascalCase()`() {
        val testCases = mapOf(
            "PascalCase" to "PascalCase",
            "camelCase" to "CamelCase",
            "kebab-case" to "KebabCase",
            "snake_case" to "SnakeCase",
            "" to ""
        )

        testCases.forEach { (input, expected) ->
            assertEquals(
                expected,
                input.toPascalCase()
            )
        }
    }
    @Test
    fun `toCamelCase()`() {
        val testCases = mapOf(
            "camelCase" to "camelCase",
            "PascalCase" to "pascalCase",
            "kebab-case" to "kebabCase",
            "snake_case" to "snakeCase",
            "" to ""
        )

        testCases.forEach { (input, expected) ->
            assertEquals(
                expected,
                input.toCamelCase()
            )
        }
    }
}
