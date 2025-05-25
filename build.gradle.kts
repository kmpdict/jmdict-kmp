// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.detekt) apply false
}

val detektMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
    val reportTree = fileTree(baseDir = rootDir) {
        include("**/detekt/main.sarif")
    }
    input.from(reportTree)
}
