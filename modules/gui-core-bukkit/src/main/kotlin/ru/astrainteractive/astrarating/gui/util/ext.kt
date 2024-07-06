@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import kotlin.math.max
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrarating.core.EmpireConfig

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

internal fun EmpireConfig.Gui.Buttons.Button.toItemStack(): ItemStack {
    val material = Material.getMaterial(material.uppercase()) ?: Material.PAPER
    val itemStack = ItemStack(material)
    itemStack.editMeta { meta ->
        meta.setCustomModelData(customModelData)
    }
    return itemStack
}
