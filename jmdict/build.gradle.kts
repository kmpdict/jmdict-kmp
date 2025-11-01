import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.detekt)
    id("com.boswelja.edrdg.generator")
    id("com.boswelja.publish")
}

kotlin {
    jvmToolchain(21)
    jvm {
        compilations.create("benchmark") {
            associateWith(this@jvm.compilations.getByName("main"))
        }
    }
    androidLibrary {
        namespace = "com.boswelja.jmdict"
        compileSdk = 36
        minSdk = 23

        withDeviceTest {}

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(libs.kotlinx.serialization.xml)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("jvmBenchmark").dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    basePath = rootDir.absolutePath
}

edrdgDict {
    dictUrl = URI("ftp://ftp.edrdg.org/pub/Nihongo/JMdict.gz")
    packageName = "com.boswelja.jmdict"
}

benchmark {
    targets {
        register("jvmBenchmark")
    }
}

publish {
    description = "Pre-packaged Japanese-Multilingual dictionary for all your Kotlin Multiplatform needs!"
    repositoryUrl = "https://github.com/kmpdict/edrdg-kmp/tree/main/jmdict"
    license = "CC-BY-SA-4.0"
}
