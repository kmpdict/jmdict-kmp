# JMnedict-KMP

Kotlin Multiplatform, pre-packaged Japanese-Multilingual name dictionary!

All dictionary-related data comes from [The JMnedict Project](https://www.edrdg.org/enamdict/enamdict_doc.html), huge shoutout to them for making this possible!

## Setup

The library is published to BOTH GitHub Packages AND Maven Central! Add it to your project with:

```kt
dependencies {
    implementation("io.github.boswelja.jmnedict:jmnedict:$version")
}
```

## Versioning

We are currently publishing `dev` versions for Android and JVM platforms, with more on the way!

Versions are date-based, and are calculated as `YYYY.MM.DD`. Dev versions are suffixed with `-dev`,
for example `2025.05.25-dev`. Dev versions are more prone to breaking changes compared to stable
versions, and are used to pilot large changes. Tests must pass for any release, but not all code in
dev may have tests.

## Usage

On any platform, call `streamJmneDict()` to get a sequence of JMnedict entries `Sequence<Entry>`, like so:

```kt
suspend fun main() {
    streamJmneDict().forEach { entry ->
        // `entry` is a dictionary element
    }
}
```

We recommend taking these elements and storing them in a database of some kind for later use.

At the time of writing, the set of all entries totals well over 100mb in memory. It's strongly
recommended to NOT collect these all at once, and instead process them one-by-one or in smaller
batches! This is especially true on mobile devices, where memory is heavily constrained.
