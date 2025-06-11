package com.boswelja.jmdict.generator.dataclass

fun String.toPascalCase(): String {
    return this
        .split("-", "_")
        .joinToString(separator = "") { segment ->
            val loweredSegment = if (segment.all { it.isUpperCase() }) {
                // If it looks like the segment is SCREAMING, then lower it
                segment.lowercase()
            } else {
                segment
            }
            loweredSegment.replaceFirstChar { it.uppercaseChar() }
        }
}

fun String.toCamelCase(): String {
    return this.toPascalCase().replaceFirstChar { it.lowercaseChar() }
}

fun String.stripPrefix(separator: String = ":"): String {
    return if (this.contains(separator)) {
        this.split(separator)[1]
    } else {
        this
    }
}