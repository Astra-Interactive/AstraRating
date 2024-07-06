package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrarating.gui.util.desc
import ru.astrainteractive.astrarating.gui.util.toItemStack
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.astrarating.model.UsersRatingsSort

internal class SortSlots(
    slotContext: SlotContext,
    private val translationContext: KyoriComponentSerializer
) : SlotContext by slotContext {

    fun ratingsSortSlot(sort: UsersRatingsSort, onClick: () -> Unit): InventorySlot = InventorySlot.Builder()
        .setIndex(50)
        .setItemStack(config.gui.buttons.sort.toItemStack())
        .editMeta {
            translationContext
                .toComponent("${translation.sort.raw}: ${sort.desc.toString(translation).raw}")
                .run(::displayName)
        }
        .setOnClickListener { onClick.invoke() }
        .build()

    fun playerRatingsSortSlot(sort: UserRatingsSort, click: Click) = InventorySlot.Builder()
        .setIndex(50)
        .setItemStack(config.gui.buttons.sort.toItemStack())
        .editMeta {
            translationContext
                .toComponent("${translation.sortRating.raw}: ${sort.desc.toString(translation).raw}")
                .run(::displayName)
        }
        .setOnClickListener(click)
        .build()
}
