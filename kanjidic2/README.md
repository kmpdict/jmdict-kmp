# KanjiDic2-KMP

Kotlin Multiplatform, pre-packaged Kanji dictionary!

All dictionary-related data comes from [The KanjiDict Project](http://www.edrdg.org/kanjidic/kanjd2index_legacy.html), huge shoutout to them for making this possible!

## Setup

The library is published to BOTH GitHub Packages AND Maven Central! Add it to your project with:

```kt
dependencies {
    implementation("io.github.boswelja.kanjidict:kanjidic2:$version")
}
```

## Versioning

We are currently publishing `dev` versions for Android and JVM platforms, with more on the way!

Versions are date-based, and are calculated as `YYYY.MM.DD`. Dev versions are suffixed with `-dev`,
for example `2025.05.25-dev`. Dev versions are more prone to breaking changes compared to stable
versions, and are used to pilot large changes. Tests must pass for any release, but not all code in
dev may have tests.

## Usage

On any platform, call `streamKanjiDict()` to get a sequence of KanjiDict entries `Sequence<Character>`, like so:

```kt
suspend fun main() {
    streamKanjiDict().forEach { character ->
        // `character` is a dictionary element
    }
}
```

We recommend taking these elements and storing them in a database of some kind for later use.
