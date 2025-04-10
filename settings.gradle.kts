pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.settings") version("8.9.1")
}

android {
    buildToolsVersion = "36.0.0"
    compileSdk = 36
    targetSdk = 36
    minSdk = 28
}

rootProject.name = "jmdict-kmp"
include(
    ":dtd"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
