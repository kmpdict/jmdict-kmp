plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.benchmark)
    id("com.boswelja.jmdict.generator")
    id("com.boswelja.publish")
}

kotlin {
    jvmToolchain(21)
    jvm {
        compilations.create("benchmark") {
            associateWith(this@jvm.compilations.getByName("main"))
        }
    }
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.xml)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("jvmBenchmark").dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)

            implementation(libs.androidx.test.core)
            implementation(libs.androidx.test.runner)
        }
    }
}

android {
    namespace = "com.boswelja.jmdict"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

jmDict {
    packageName = "com.boswelja.jmdict"
}

benchmark {
    targets {
        register("jvmBenchmark")
    }
}

publish {
    description = "Pre-packaged Japanese-Multilingual dictionary for all your Kotlin Multiplatform needs!"
    repositoryUrl = "https://github.com/boswelja/jmdict-kmp"
    license = "MIT"
}
