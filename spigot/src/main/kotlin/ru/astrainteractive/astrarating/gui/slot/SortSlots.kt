package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
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

    fun ratingsSortSlot(sort: UsersRatingsSort, onClick: () -> Unit): InventorySlot = InventorySlot.Builder {
        index = 50
        itemStack = config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                translationContext
                    .toComponent("${translation.sort}: ${sort.desc.toString(translation)}")
                    .run(it::displayName)
            }
        }
        click = Click { onClick.invoke() }
    }

    fun playerRatingsSortSlot(sort: UserRatingsSort, click: Click) = InventorySlot.Builder {
        index = 50
        itemStack = config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                translationContext
                    .toComponent("${translation.sortRating}: ${sort.desc.toString(translation)}")
                    .run(it::displayName)
            }
        }
        this.click = click
    }
}
