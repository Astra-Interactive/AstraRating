package ru.astrainteractive.astrarating.gui.bukkit.util

import kotlin.math.max

internal fun splitMessageLines(text: String, threshold: Int, cutWords: Boolean): List<String> {
    if (cutWords) return text.chunked(threshold)
    return text.split(" ")
        .chunked(max(1, text.length / threshold))
        .map { words -> words.joinToString(" ") }
}
