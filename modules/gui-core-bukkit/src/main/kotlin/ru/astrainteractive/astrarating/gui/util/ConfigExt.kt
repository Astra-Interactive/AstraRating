package ru.astrainteractive.astrarating.gui.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig

internal fun AstraRatingConfig.Gui.Buttons.Button.toItemStack(): ItemStack {
    val material = Material.getMaterial(material.uppercase()) ?: Material.PAPER
    val itemStack = ItemStack(material)
    itemStack.editMeta { meta ->
        meta.setCustomModelData(customModelData)
    }
    return itemStack
}