package com.boswelja.edrdg.generator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.net.URI
import kotlin.reflect.KProperty0

internal const val ExtensionName: String = "edrdgDict"

interface EdrdgExtension {

    /**
     * The URL for the full EDRDG archive. Defaults to `ftp://ftp.edrdg.org/pub/Nihongo/JMdict.gz`.
     */
    val dictUrl: Property<URI>

    /**
     * The package name for the generated sources.
     */
    val packageName: Property<String>

    /**
     * Whether additional metadata, such as entry count, date information, and changelog should be
     * captured. When set to `true`, a `data class Metadata` is generated alongside jmdict content.
     * Defaults to `true`.
     */
    val generateMetadata: Property<Boolean>
}

class JmDictGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Create the Gradle extension for configuration
        val config = target.extensions.create(
            ExtensionName,
            EdrdgExtension::class.java
        )
        config.generateMetadata.convention(true)

        val dictFile = target.layout.buildDirectory.file("resources/edrdg/dict.xml")
        val relNotesFile = target.layout.buildDirectory.file("resources/edrdg/changelog.xml")
        val dtdFile = target.layout.buildDirectory.file("resources/edrdg/dtd.xml")
        val metadataFile = target.layout.buildDirectory.file("resources/edrdg/metadata.properties")

        // Register the download task
        val downloadDictTask = target.tasks.register(
            "downloadDict",
            DownloadDictTask::class.java
        ) {
            requireProperty(config::dictUrl, "ftp://ftp.edrdg.org/pub/Nihongo/JMdict.gz")

            it.dictUrl.set(config.dictUrl)
            it.outputDict.set(dictFile)
            it.outputDtd.set(dtdFile)
            it.outputReleaseNotes.set(relNotesFile)
            it.outputMetadata.set(metadataFile)
        }

        target.afterEvaluate {
            // Configure KMP projects
            target.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?.configureKmp(target, config, downloadDictTask)
        }
    }

    private fun KotlinMultiplatformExtension.configureKmp(
        target: Project,
        config: EdrdgExtension,
        downloadDictTask: TaskProvider<DownloadDictTask>,
    ) {
        val targetGeneratedSourcesDir = target.layout.buildDirectory.dir("generated/jmDict/kotlin")

        // Register the generation tasks
        val generateDataClassTask = target.tasks.register(
            "generateDictDataClasses",
            GenerateDataClassesTask::class.java
        ) {
            requireProperty(config::packageName, "\"com.my.package\"")

            it.dependsOn(downloadDictTask)

            it.outputDirectory.set(targetGeneratedSourcesDir)
            it.packageName.set(config.packageName)
            it.dtdFile.set(downloadDictTask.get().outputDtd)
        }
        val generateMetadataTask = target.tasks.register(
            "generateDictMetadataObject",
            GenerateMetadataObjectTask::class.java
        ) {
            requireProperty(config::packageName, "\"com.my.package\"")

            it.dependsOn(downloadDictTask)

            it.outputDirectory.set(targetGeneratedSourcesDir)
            it.packageName.set(config.packageName)
            it.metadataFile.set(downloadDictTask.get().outputMetadata)
        }

        this.targets.configureEach {
            when (it.name) {
                "metadata" -> {
                    // Add the generated source dir to the common source set
                    sourceSets.commonMain.configure {
                        it.kotlin.srcDir(generateDataClassTask.map { it.outputDirectory })
                        if (config.generateMetadata.get()) {
                            it.kotlin.srcDir(generateMetadataTask.map { it.outputDirectory })
                        }
                    }

                    // Add generation task as a dependency for source jar tasks
                    target.tasks.withType(Jar::class.java).configureEach {
                        if (it.archiveClassifier.get() == "sources") {
                            if (config.generateMetadata.get()) {
                                it.dependsOn(generateMetadataTask)
                            }
                            it.dependsOn(generateDataClassTask)
                        }
                    }
                }
                "jvm" -> {
                    val generatedResDir = target.layout.buildDirectory.dir("generated/edrdgDict/jvmMainResources")
                    val copyResTask = target.tasks.register("copyJvmMainDictResources", CopyResourcesTask::class.java) {
                        it.jmDictFile.set(downloadDictTask.get().outputDict)
                        it.outputDirectory.set(generatedResDir)
                        it.dependsOn(downloadDictTask)
                    }
                    sourceSets.jvmMain.configure { it.resources.srcDir(copyResTask.map { it.outputDirectory }) }
                }
                "android" -> {
                    val generatedResDir = target.layout.buildDirectory.dir("generated/edrdgDict/androidMainResources")
                    val copyResTask = target.tasks.register("copyAndroidMainDictResources", CopyAndroidResourcesTask::class.java) {
                        it.jmDictFile.set(downloadDictTask.get().outputDict)
                        it.outputDirectory.set(generatedResDir)
                        it.dependsOn(downloadDictTask)
                    }
                    sourceSets.androidMain.configure { it.resources.srcDir(copyResTask.map { it.outputDirectory }) }
                }
                else -> error("Unknown target ${it.name}")
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
