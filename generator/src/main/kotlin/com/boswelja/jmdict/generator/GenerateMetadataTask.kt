package com.boswelja.jmdict.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.compose.internal.com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.compose.internal.com.squareup.kotlinpoet.FileSpec
import org.jetbrains.compose.internal.com.squareup.kotlinpoet.PropertySpec
import org.jetbrains.compose.internal.com.squareup.kotlinpoet.TypeSpec
import java.util.Properties
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

abstract class GenerateMetadataObjectTask : DefaultTask() {

    /**
     * The directory to store generated source files in.
     */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    /**
     * The package name for the generated sources.
     */
    @get:Input
    abstract val packageName: Property<String>

    @get:InputFile
    abstract val metadataFile: RegularFileProperty

    @OptIn(ExperimentalTime::class)
    @TaskAction
    fun generateDataClasses() {
        val metadataObject = TypeSpec.objectBuilder("Metadata")
        val props = Properties()
        metadataFile.get().asFile.inputStream().use {
            props.load(it)
        }
        metadataObject.addProperty(
            PropertySpec.builder("entryCount", Int::class)
                .initializer("%L", props["entryCount"])
                .addKdoc("%S", "The number of entries that the dataset contains. One entry is equivalent to one word.")
                .build()
        )
        val instant = Instant.parse(props["timeUtc"].toString())
        metadataObject.addProperty(
            PropertySpec.builder("createdAt", Instant::class)
                .initializer("Instant.fromEpochSeconds(%L)", instant.epochSeconds)
                .addAnnotation(ExperimentalTime::class)
                .addKdoc("%S", "The Instant at which the dataset was created, accurate to the second.")
                .build()
        )
        FileSpec.builder(packageName.get(), "Metadata")
            .addType(metadataObject.build())
            .addImport("kotlin.time", "Instant")
            .build()
            .writeTo(outputDirectory.get().asFile)
    }
}
