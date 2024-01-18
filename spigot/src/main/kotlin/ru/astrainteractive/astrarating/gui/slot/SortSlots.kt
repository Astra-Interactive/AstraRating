package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.editMeta
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.gui.util.desc
import ru.astrainteractive.astrarating.gui.util.toItemStack
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.astrarating.model.UsersRatingsSort

class SortSlots(
    slotContext: SlotContext,
    private val menu: PaginatedMenu,
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
