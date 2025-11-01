package com.boswelja.edrdg.core

import okio.BufferedSource

public fun BufferedSource.readLines(): Sequence<String> {
    return sequence {
        while (!this@readLines.exhausted()) {
            yield(readUtf8Line()!!)
        }
    }
}
