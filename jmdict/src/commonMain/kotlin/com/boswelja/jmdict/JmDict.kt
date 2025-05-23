package com.boswelja.jmdict

import nl.adaptivity.xmlutil.serialization.XML

internal val Serializer = XML {
    defaultPolicy {
        pedantic = true
        autoPolymorphic = true
        throwOnRepeatedElement = true
        isStrictBoolean = true
        isStrictAttributeNames = true
        isXmlFloat = true
        verifyElementOrder = true
    }
}

expect class JmDictReader {
    suspend fun streamJmDict(): Sequence<Entry>
}

internal fun <T> Sequence<T>.chunkedUntil(predicate: (T) -> Boolean): Sequence<List<T>> {
    return sequence {
        val list = mutableListOf<T>()
        this@chunkedUntil.forEach {
            if (!predicate(it)) {
                list.add(it)
            } else {
                yield(list.toList())
                list.clear()
                list.add(it)
            }
        }
    }
}
