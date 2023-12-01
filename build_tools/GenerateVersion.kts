#!/usr/bin/env kscript

import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import kotlin.random.Random

fun getGradleVersion(): String {
    val version = File("../build.gradle.kts").readLines().filter {
        it.contains("version = ")
    }.firstOrNull() ?: throw Exception("Unable to find version")

    return version.trim().replace("version = ", "").replace("\"", "")
}

fun generateName(c: Int): String {
    val adjectives = listOf(
        "aged", "ancient", "autumn", "billowing", "bitter", "black", "blue", "bold",
        "broad", "broken", "calm", "cold", "cool", "crimson", "curly", "damp",
        "dark", "dawn", "delicate", "divine", "dry", "empty", "falling", "fancy",
        "flat", "floral", "fragrant", "frosty", "gentle", "green", "hidden", "holy",
        "icy", "jolly", "late", "lingering", "little", "lively", "long", "lucky",
        "misty", "morning", "muddy", "mute", "nameless", "noisy", "odd", "old",
        "orange", "patient", "plain", "polished", "proud", "purple", "quiet", "rapid",
        "raspy", "red", "restless", "rough", "round", "royal", "shiny", "shrill",
        "shy", "silent", "small", "snowy", "soft", "solitary", "sparkling", "spring",
        "square", "steep", "still", "summer", "super", "sweet", "throbbing", "tight",
        "tiny", "twilight", "wandering", "weathered", "white", "wild", "winter", "wispy",
        "withered", "yellow", "young"
    )

    val nouns = listOf(
        "art", "band", "bar", "base", "bird", "block", "boat", "bonus",
        "bread", "breeze", "brook", "bush", "butterfly", "cake", "cell", "cherry",
        "cloud", "credit", "darkness", "dawn", "dew", "disk", "dream", "dust",
        "feather", "field", "fire", "firefly", "flower", "fog", "forest", "frog",
        "frost", "glade", "glitter", "grass", "hall", "hat", "haze", "heart",
        "hill", "king", "lab", "lake", "leaf", "limit", "math", "meadow",
        "mode", "moon", "morning", "mountain", "mouse", "mud", "night", "paper",
        "pine", "poetry", "pond", "queen", "rain", "recipe", "resonance", "rice",
        "river", "salad", "scene", "sea", "shadow", "shape", "silence", "sky",
        "smoke", "snow", "snowflake", "sound", "star", "sun", "sun", "sunset",
        "surf", "term", "thunder", "tooth", "tree", "truth", "union", "unit",
        "violet", "voice", "water", "water", "waterfall", "wave", "wildflower", "wind",
        "wood"
    )

    val random = Random(20231122)

    var shuffledAdjectives = adjectives.shuffled(random)
    var shuffledNouns = nouns.shuffled(random)

    var adjective = shuffledAdjectives.first()
    var noun = shuffledNouns.first()

    val seen = mutableSetOf<String>()

    for (i in 0 .. c) {
        while (true) {
            adjective = shuffledAdjectives.first()
            noun = shuffledNouns.first()

            shuffledAdjectives = shuffledAdjectives.drop(1)
            shuffledNouns = shuffledNouns.drop(1)

            if (shuffledAdjectives.isEmpty()) shuffledAdjectives = adjectives.shuffled(random)
            if (shuffledNouns.isEmpty()) shuffledNouns = nouns.shuffled(random)

            if (seen.contains("$adjective $noun")) continue
            seen.add("$adjective $noun")
            break
        }
    }

    return "${adjective.replaceFirstChar { it.uppercase() }} ${noun.replaceFirstChar { it.uppercase() }}"
}

val versionCount = File("../VERSIONS").readLines().count { it.isNotEmpty() }

val version = getGradleVersion()
val date = LocalDate.now().toString()
val name = generateName(versionCount)

val versionOutput = File("../VERSIONS")
    .readLines()
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .toMutableList()
    .apply { add("$version\t$date\t$name") }

FileWriter("../VERSIONS").use { writer ->
    versionOutput.forEach {
        writer.append(it)
        writer.appendLine()
    }
}
