package com.boswelja.jmdict.generator

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class CopyComposeResourcesTask : DefaultTask() {

    @get:InputFile
    abstract val jmDictFile: RegularFileProperty

    /**
     * The directory to store generated source files in.
     */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun copyJmDictToResources() {
        fs.copy(Action { t ->
            t.from(jmDictFile)
            t.into(outputDirectory.dir("files/"))
        })
    }
}
