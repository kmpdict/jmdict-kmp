plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
}

gradlePlugin {
    plugins {
        create("com.boswelja.edrdg") {
            id = "com.boswelja.edrdg.generator"
            implementationClass = "com.boswelja.edrdg.generator.JmDictGeneratorPlugin"
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.kotlin.plugin)
    implementation(libs.android.libraryGradlePlugin)

    implementation(libs.boswelja.xmldtd)
    implementation(libs.kotlinx.io.core)
    implementation(libs.okio.core)
    implementation(libs.okio.zstd)
    implementation(libs.kotlinpoet)

    testImplementation(libs.kotlin.test)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("${rootDir.parent}/config/detekt.yml")
    basePath = rootDir.absolutePath
}
