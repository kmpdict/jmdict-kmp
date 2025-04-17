package com.boswelja.jmdict.generator.dataclass

fun String.toPascalCase(): String {
    return this
        .split("-", "_")
        .joinToString(separator = "") { segment ->
            segment.replaceFirstChar { it.uppercaseChar() }
        }
}

fun String.toCamelCase(): String {
    return this.toPascalCase().replaceFirstChar { it.lowercaseChar() }
}
