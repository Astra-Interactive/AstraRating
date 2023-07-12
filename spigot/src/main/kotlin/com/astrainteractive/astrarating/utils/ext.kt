@file:Suppress("Filename")

package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.modules.impl.RootModuleImpl
import kotlin.math.max

fun subListFromString(text: String, threshold: Int): List<String> {
    val res = if (RootModuleImpl.config.value.cutWords) {
        text.chunked(threshold)
    } else {
        text.split(" ").chunked(max(1, (text.length) / threshold)).map {
            it.joinToString(" ")
        }
    }
    return res
}
