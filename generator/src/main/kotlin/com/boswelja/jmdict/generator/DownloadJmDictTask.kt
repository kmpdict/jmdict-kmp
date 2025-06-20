package com.boswelja.jmdict.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.properties.saveToFile
import java.net.URI
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Properties
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

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

    @get:OutputFile
    abstract val outputMetadata: RegularFileProperty

    @TaskAction
    fun downloadAndUnpackJmDict() {
        val jmDictStream = GZIPOutputStream(outputJmDict.get().asFile.outputStream()).writer()
        val releaseNotesOutputStream = outputReleaseNotes.get().asFile.outputStream().writer()
        val dtdOutputStream = outputDtd.get().asFile.outputStream().writer()

        val urlConnection = jmDictUrl.get().toURL().openConnection()
        val inStream = GZIPInputStream(urlConnection.inputStream).bufferedReader()

        // Metadata fields
        var entryCount = 0

        try {
            // Download jmdict and write to separate files
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
                    if (line.startsWith("<entry>")) entryCount++
                    jmDictStream.appendLine(line)
                }
                line = inStream.readLine()
            }

            // Write captured metadata
            val props = Properties()
            props.setProperty("entryCount", entryCount.toString())
            props.setProperty("timeUtc", OffsetDateTime.now(ZoneId.of("UTC")).toString())
            outputMetadata.get().asFile.outputStream().use {
                props.store(it, null)
            }
        } finally {
            jmDictStream.close()
            releaseNotesOutputStream.close()
            dtdOutputStream.close()
            inStream.close()
        }
    }
}
