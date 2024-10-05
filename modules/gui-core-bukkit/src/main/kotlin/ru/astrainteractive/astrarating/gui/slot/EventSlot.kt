package ru.astrainteractive.astrarating.gui.slot

import org.bukkit.Material
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setMaterial
import ru.astrainteractive.astrarating.gui.slot.context.SlotContext

internal fun SlotContext.killEventSlot(killCounts: Int) = InventorySlot.Builder()
    .setIndex(46)
    .setMaterial(Material.NETHERITE_SWORD)
    .editMeta {
        translation.eventsTitle
            .component
            .run(::displayName)
    }
    .addLore(translation.eventKillAmount(killCounts).component)
    .build()
    .takeIf { killCounts > 0 }
