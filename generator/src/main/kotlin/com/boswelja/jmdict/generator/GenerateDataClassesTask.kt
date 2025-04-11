package com.boswelja.jmdict.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory

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
}
