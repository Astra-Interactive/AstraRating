package ru.astrainteractive.astrarating.feature.gui.slot

import org.bukkit.Material
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setMaterial
import ru.astrainteractive.astrarating.feature.gui.slot.context.SlotContext

internal fun SlotContext.killEventSlot(index: Int, killCounts: Int) = InventorySlot.Builder()
    .setIndex(index = index)
    .setMaterial(Material.NETHERITE_SWORD)
    .editMeta {
        translation.playerRatingsMenu.eventsTitle
            .toComponent(locale)
            .run(::displayName)
    }
    .addLore(translation.playerRatingsMenu.killCount(killCounts).toComponent(locale))
    .build()
    .takeIf { killCounts > 0 }
