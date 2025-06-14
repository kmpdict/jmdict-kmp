package com.boswelja.jmdict.generator

import com.boswelja.xmldtd.codegen.DataClassGenerator
import com.boswelja.xmldtd.deserialize.DocumentTypeDefinition
import com.boswelja.xmldtd.deserialize.fromSource
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
    abstract val dtdFile: RegularFileProperty

    @TaskAction
    fun generateDataClasses() {
        val generator = DataClassGenerator(packageName.get(), outputDirectory.get().asFile.toPath())
        val jmDict = dtdFile.get().asFile.inputStream().asSource().buffered()
        val definition = DocumentTypeDefinition.fromSource(jmDict)
        generator.writeDtdToTarget(definition)
    }
}
