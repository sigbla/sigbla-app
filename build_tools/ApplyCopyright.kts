#!/usr/bin/env kscript

import java.io.File
import java.io.FileWriter

val ktCopyrightNotice = listOf(
    "/* Copyright 2019-2023, Christian Felde.",
    " * See LICENSE file for licensing details. */"
)

val folders = listOf(
    File("../all/src"),
    File("../app/src"),
    File("../charts/src"),
    File("../examples/src"),
    File("../widgets/src")
)

fun applyLicense(target: File): Int {
    if (!target.exists()) return 0

    if (target.isDirectory) {
        var changedFiles = 0

        for (child in target.listFiles()) {
            changedFiles += applyLicense(child)
        }

        return changedFiles
    }

    if (target.isFile && target.name.endsWith(".kt")) {
        println("Checking ${target.path}")

        val content = target.readLines()

        if (content.size < ktCopyrightNotice.size) return 0

        var match = true
        for (i in ktCopyrightNotice.indices) {
            if (content[i] != ktCopyrightNotice[i]) {
                match = false
                break
            }
        }

        if (match) return 0

        println("Missing on ${target.path}")

        val codeContent = content.dropWhile { !it.startsWith("package") }

        if (codeContent.isEmpty()) throw UnsupportedOperationException("No package")

        val newTarget = File(target.parentFile, target.name + ".tmp")

        FileWriter(newTarget).use { writer ->
            ktCopyrightNotice.forEach {
                writer.append(it)
                writer.appendLine()
            }
            codeContent.forEach {
                writer.append(it)
                writer.appendLine()
            }

            writer.flush()
        }

        newTarget.renameTo(target)

        return 1
    }

    return 0
}

fun otherLicense(target: File): Int {
    if (!target.exists()) return 0

    if (target.isDirectory) {
        var changedFiles = 0

        for (child in target.listFiles()) {
            changedFiles += otherLicense(child)
        }

        return changedFiles
    }

    if (target.isFile && target.name.endsWith(".js")) {
        println("For manually check: ${target.path}")

        return 1
    }

    return 0
}

var changedFiles = 0

for (target in folders) {
    changedFiles += applyLicense(target)
}

var manualFiles = 0

for (target in folders) {
    manualFiles += otherLicense(target)
}

println("Done, $changedFiles files changed")
println("Also, please verify $manualFiles files..")