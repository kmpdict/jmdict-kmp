import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.boswelja.jmdict.generator")
}

kotlin {
    jvmToolchain(21)
    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(libs.kotlinx.serialization.xml)
        }
    }
}

android {
    namespace = "com.boswelja.jmdict"
}

jmDict {
    packageName = "com.boswelja.jmdict"
}
