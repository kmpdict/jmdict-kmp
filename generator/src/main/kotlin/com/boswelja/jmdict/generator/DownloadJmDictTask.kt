package com.boswelja.jmdict.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.util.zip.GZIPInputStream

abstract class DownloadJmDictTask : DefaultTask() {

    /**
     * The URL for the full JMDict archive.
     */
    @get:Input
    abstract val jmDictUrl: Property<URI>

    /**
     * The directory to store the downloaded content.
     */
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun downloadAndUnpackJmDict() {
        outputFile.get().asFile.outputStream().use { out ->
            GZIPInputStream(jmDictUrl.get().toURL().openStream()).use { inp ->
                inp.copyTo(out)
            }
        }
    }
}
