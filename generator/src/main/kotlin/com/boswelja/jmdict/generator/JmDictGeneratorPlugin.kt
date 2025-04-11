package com.boswelja.jmdict.generator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import kotlin.reflect.KProperty0

internal const val ExtensionName: String = "jmDict"

interface JmDictExtension {

    /**
     * The URL for the full JMDict archive.
     */
    val jmDictUrl: Property<URI>

    /**
     * The package name for the generated sources.
     */
    val packageName: Property<String>
}

class JmDictGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Create the Gradle extension for configuration
        val config = target.extensions.create(
            ExtensionName,
            JmDictExtension::class.java
        )
        config.jmDictUrl.convention(URI("http://ftp.edrdg.org/pub/Nihongo/JMdict.gz"))

        val targetGeneratedSourcesDir = target.layout.buildDirectory.dir("generated/jmdict/")
        val extractedJmDictDir = target.layout.buildDirectory.file("resources/jmdict/jmdict.xml")

        // Configure KMP projects
        target.extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {
            // Register the download task
            val downloadJmDictTask = target.tasks.register(
                "downloadJmDict",
                DownloadJmDictTask::class.java
            ) {
                it.jmDictUrl.set(config.jmDictUrl)
                it.outputFile.set(extractedJmDictDir)
            }

            // Register the generation tasks
            val generateDataClassTask = target.tasks.register(
                "generateJmDictDataClasses",
                GenerateDataClassesTask::class.java
            ) {
                requireProperty(config::packageName, "\"com.my.package\"")

                it.dependsOn(downloadJmDictTask)

                it.outputDirectory.set(targetGeneratedSourcesDir)
                it.packageName.set(config.packageName)
                it.jmDictXml.set(downloadJmDictTask.get().outputFile)
            }
            val generateDatabaseTask = target.tasks.register("generateJmDictDatabase") {
                requireProperty(config::packageName, "\"com.my.package\"")

                it.dependsOn(downloadJmDictTask)
            }

            // Add generation tasks as dependencies for build task
            target.tasks.withType(KotlinCompile::class.java).configureEach {
                it.dependsOn(generateDatabaseTask, generateDataClassTask)
            }

            // Add the generated source dir to the common source set
            sourceSets.commonMain.configure {
                it.kotlin.srcDir(targetGeneratedSourcesDir)
            }
        }
    }
}

private fun requireProperty(property: KProperty0<Property<*>>, exampleValue: String) {
    require(property.get().isPresent) {
        """$ExtensionName.${property.name} must be specified.
               |$ExtensionName {
               |    ${property.name} = $exampleValue
               |}""".trimMargin()
    }
}
