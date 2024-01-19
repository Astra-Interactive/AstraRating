@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrarating.core.EmpireConfig
import kotlin.math.max

fun subListFromString(text: String, threshold: Int, cutWords: Boolean): List<String> {
    val res = if (cutWords) {
        text.chunked(threshold)
    } else {
        text.split(" ").chunked(max(1, (text.length) / threshold)).map {
            it.joinToString(" ")
        }
    }
    return res
}

fun EmpireConfig.Gui.Buttons.Button.toItemStack(): ItemStack {
    return ItemStack(Material.getMaterial(material.uppercase()) ?: Material.PAPER).apply {
        val meta = itemMeta!!
        meta.setCustomModelData(customModelData)
        itemMeta = meta
    }
}
