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

rootProject.name = "edrdg-kmp"

includeBuild("generator")

include(
    ":core",
    ":jmdict",
    ":jmnedict",
    "kanjidic2",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
