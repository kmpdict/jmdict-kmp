plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
}

gradlePlugin {
    plugins {
        create("com.boswelja.jmdict") {
            id = "com.boswelja.jmdict.generator"
            implementationClass = "com.boswelja.jmdict.generator.JmDictGeneratorPlugin"
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.kotlin.plugin)
    implementation(libs.compose.gradlePlugin)

    implementation(libs.boswelja.xmldtd)
    implementation(libs.kotlinx.io.core)

    testImplementation(libs.kotlin.test)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("${rootDir.parent}/config/detekt.yml")
    basePath = rootDir.absolutePath
}
