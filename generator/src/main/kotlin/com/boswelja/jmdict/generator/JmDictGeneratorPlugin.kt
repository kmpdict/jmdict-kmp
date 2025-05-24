package com.boswelja.jmdict.generator

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
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
        config.jmDictUrl.convention(URI("ftp://ftp.edrdg.org/pub/Nihongo/JMdict.gz"))

        val targetGeneratedSourcesDir = target.layout.buildDirectory.dir("generated/jmdict/kotlin")
        val jmDictFile = target.layout.buildDirectory.file("resources/jmdict/jmdict.xml")
        val relNotesFile = target.layout.buildDirectory.file("resources/jmdict/changelog.xml")
        val dtdFile = target.layout.buildDirectory.file("resources/jmdict/dtd.xml")

        // Register the download task
        val downloadJmDictTask = target.tasks.register(
            "downloadJmDict",
            DownloadJmDictTask::class.java
        ) {
            requireProperty(config::jmDictUrl, "ftp://ftp.edrdg.org/pub/Nihongo/JMdict.gz")

            it.jmDictUrl.set(config.jmDictUrl)
            it.outputJmDict.set(jmDictFile)
            it.outputDtd.set(dtdFile)
            it.outputReleaseNotes.set(relNotesFile)
        }

        // Configure JVM projects
        target.extensions.findByType(KotlinJvmExtension::class.java)?.apply {
            val targetGeneratedSourcesDir = target.layout.buildDirectory.dir("generated/jmdict/jvmMain/resources/")
            // Register the generation tasks
            val generateDataClassTask = target.tasks.register(
                "generateJmDictDataClasses",
                GenerateDataClassesTask::class.java
            ) {
                requireProperty(config::packageName, "\"com.my.package\"")

                it.dependsOn(downloadJmDictTask)

                it.outputDirectory.set(targetGeneratedSourcesDir)
                it.packageName.set(config.packageName)
                it.dtdFile.set(downloadJmDictTask.get().outputDtd)
            }

            // Add generation tasks as dependencies for build task
            target.tasks.withType(KotlinCompile::class.java).configureEach {
                it.dependsOn(generateDataClassTask)
            }

            // Add the generated source dir to the common source set
            sourceSets.getByName("main").apply {
                kotlin.srcDir(targetGeneratedSourcesDir)
            }

        }
        // Configure KMP projects
        target.extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {

            // Register the generation tasks
            val generateDataClassTask = target.tasks.register(
                "generateJmDictDataClasses",
                GenerateDataClassesTask::class.java
            ) {
                requireProperty(config::packageName, "\"com.my.package\"")

                it.dependsOn(downloadJmDictTask)

                it.outputDirectory.set(targetGeneratedSourcesDir)
                it.packageName.set(config.packageName)
                it.dtdFile.set(downloadJmDictTask.get().outputDtd)
            }

            // Add generation tasks as dependencies for build task
            target.tasks.withType(KotlinCompile::class.java).configureEach {
                it.dependsOn(generateDataClassTask)
            }

            // Add the generated source dir to the common source set
            sourceSets.commonMain.configure {
                it.kotlin.srcDir(targetGeneratedSourcesDir)
            }

            if (sourceSets.jvmMain.isPresent) {
                sourceSets.jvmMain.configure {
                    val targetGeneratedSourcesDir = target.layout.buildDirectory.dir("generated/jmdict/jvmMain/resources/")
                    it.resources.srcDir(targetGeneratedSourcesDir)

                    val copyResourcesTask = target.tasks.register(
                        "copyJvmMainJmDictResource",
                        CopyJvmResourcesTask::class.java
                    ) {
                        it.dependsOn(downloadJmDictTask)
                        it.jmDictFile.set(jmDictFile)
                        it.outputDirectory.set(targetGeneratedSourcesDir)
                    }

                    afterEvaluate {
                        target.tasks.getByName("processJvmMainResources").dependsOn(copyResourcesTask)
                    }
                }
            } else {
                println("No jvm target")
            }
        }

        target.extensions.findByType(AndroidComponentsExtension::class.java)?.apply {
            onVariants { variant ->
                val copyResourcesTask = target.tasks.register(
                    "copy${variant.name.replaceFirstChar { it.uppercase() }}JmDictResource",
                    CopyAndroidResourcesTask::class.java
                ) {
                    it.dependsOn(downloadJmDictTask)
                    it.jmDictFile.set(jmDictFile)
                }
                variant.sources.res?.addGeneratedSourceDirectory(copyResourcesTask, CopyAndroidResourcesTask::outputDirectory)
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
