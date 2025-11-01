plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    id("com.boswelja.publish")
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    jvm ()
    androidLibrary {
        namespace = "com.boswelja.edrdg.core"
        compileSdk = 36
        minSdk = 23

        withDeviceTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.xml)
            implementation(libs.okio.core)
            implementation(libs.okio.zstd)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup)
        }
        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.robolectric:robolectric:4.16")
            implementation("androidx.test.ext:junit-ktx:1.3.0")
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    basePath = rootDir.absolutePath
}

publish {
    description = "Shared utilities for the EDRDG-KMP project"
    repositoryUrl = "https://github.com/kmpdict/edrdg-kmp/tree/main/core"
    license = "CC-BY-SA-4.0"
}
