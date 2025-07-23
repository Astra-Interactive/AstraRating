@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import kotlin.math.max

internal fun subListFromString(text: String, threshold: Int, cutWords: Boolean): List<String> {
    val res = if (cutWords) {
        text.chunked(threshold)
    } else {
        text.split(" ").chunked(max(1, (text.length) / threshold)).map {
            it.joinToString(" ")
        }
    }
    return res
}

