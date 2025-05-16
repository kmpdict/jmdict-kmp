package com.boswelja.jmdict.generator

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CopyJvmResourcesTask : DefaultTask() {

    @get:InputFile
    abstract val jmDictFile: RegularFileProperty

    /**
     * The directory to store generated source files in.
     */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun copyJmDictToResources() {
        project.copy(Action { t ->
            t.from(jmDictFile)
            t.into(outputDirectory)
        })
    }
}
