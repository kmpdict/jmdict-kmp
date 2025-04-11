import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("com.boswelja.jmdict.generator")
}

kotlin {
    jvmToolchain(21)
    jvm()
    androidTarget()
}

android {
    namespace = "com.boswelja.jmdict"
}

jmDict {
    packageName = "com.boswelja.jmdict"
    jmDictUrl = URI("https://ftp.edrdg.org/pub/Nihongo/JMdict.gz")
}
