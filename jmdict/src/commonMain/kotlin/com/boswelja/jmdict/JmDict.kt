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
    suspend fun openJmDict(): JMdict
}
