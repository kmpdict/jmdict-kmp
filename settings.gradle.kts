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
    id("com.android.settings") version("8.10.0")
}

android {
    buildToolsVersion = "36.0.0"
    compileSdk = 36
    targetSdk = 36
    minSdk = 23
}

rootProject.name = "jmdict-kmp"

includeBuild("generator")

include(":jmdict")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
