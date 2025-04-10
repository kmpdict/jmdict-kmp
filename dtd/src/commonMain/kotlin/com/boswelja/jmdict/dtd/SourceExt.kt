package com.boswelja.jmdict.dtd

import kotlinx.io.Source
import kotlinx.io.readLine

/**
 * Reads lines from the source until a predicate returns true, or there are no more lines. Lines
 * read are joined with a separator that replaces the newline character. If there are no lines to
 * read at all, null is returned.
 *
 * @param predicate A function that takes a string and returns true if the line should be included in the result.
 * @return The lines that match the predicate, or null if no lines match.
 */
internal fun Source.readLinesUntil(predicate: (String) -> Boolean): String? {
    val result = buildString {
        var line: String? = readLine()?.trim()
        while (line != null) {
            append(line)
            if (predicate(line)) {
                break
            } else if (line.isNotBlank()) {
                append(" ")
            }
            line = readLine()?.trim()
        }
    }
    return result.trim().takeIf { it.isNotEmpty() }
}
