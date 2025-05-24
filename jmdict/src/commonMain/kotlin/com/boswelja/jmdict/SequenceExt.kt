package com.boswelja.jmdict

internal fun <T> Sequence<T>.chunkedUntil(predicate: (T) -> Boolean): Sequence<List<T>> {
    return sequence {
        var list = mutableListOf<T>()
        this@chunkedUntil.forEach {
            if (!predicate(it)) {
                list.add(it)
            } else {
                yield(list)
                list = mutableListOf(it)
            }
        }
        if (list.isNotEmpty()) yield(list)
    }
}
