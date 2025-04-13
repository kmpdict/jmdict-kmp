package com.boswelja.jmdict.generator

import com.boswelja.jmdict.generator.dtd.DocumentTypeDefinition
import com.boswelja.jmdict.generator.dtd.fromSource
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateDataClassesTask : DefaultTask() {

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
    abstract val jmDictXml: RegularFileProperty

    @TaskAction
    fun generateDataClasses() {
        val jmDict = jmDictXml.get().asFile.inputStream().asSource().buffered()
        val definition = DocumentTypeDefinition.fromSource(jmDict)
    }
}
