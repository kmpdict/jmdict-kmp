plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
}

android {
    namespace = "com.boswelja.jmdict.dtd"
}

kotlin {
    jvmToolchain(21)
    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.io.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
