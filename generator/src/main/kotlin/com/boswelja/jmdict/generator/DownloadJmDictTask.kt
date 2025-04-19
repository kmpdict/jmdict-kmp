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
     * The file to store the output jmdict.
     */
    @get:OutputFile
    abstract val outputJmDict: RegularFileProperty

    @get:OutputFile
    abstract val outputDtd: RegularFileProperty

    @get:OutputFile
    abstract val outputReleaseNotes: RegularFileProperty

    @TaskAction
    fun downloadAndUnpackJmDict() {
        val jmDictStream = outputJmDict.get().asFile.outputStream().writer()
        val releaseNotesOutputStream = outputReleaseNotes.get().asFile.outputStream().writer()
        val dtdOutputStream = outputDtd.get().asFile.outputStream().writer()

        val urlConnection = jmDictUrl.get().toURL().openConnection()
        val inStream = GZIPInputStream(urlConnection.inputStream).bufferedReader()
        try {
            var finishedRelNotes = false
            var finishedDtd = false
            var line = inStream.readLine()
            while (line != null) {
                if (!finishedRelNotes) {
                    if (line.startsWith("<!DOCTYPE")) {
                        finishedRelNotes = true
                        dtdOutputStream.appendLine(line)
                    } else {
                        releaseNotesOutputStream.appendLine(line)
                    }
                } else if (!finishedDtd) {
                    dtdOutputStream.appendLine(line)
                    if (line.startsWith("]>")) {
                        finishedDtd = true
                    }
                } else {
                    jmDictStream.appendLine(line)
                }
                line = inStream.readLine()
            }
        } finally {
            jmDictStream.close()
            releaseNotesOutputStream.close()
            dtdOutputStream.close()
            inStream.close()
        }
    }
}
