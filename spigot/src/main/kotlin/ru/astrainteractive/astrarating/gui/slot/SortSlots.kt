package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrarating.gui.util.desc
import ru.astrainteractive.astrarating.gui.util.toItemStack
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.astrarating.model.UsersRatingsSort

class SortSlots(
    slotContext: SlotContext,
    private val menu: PaginatedMenu
) : SlotContext by slotContext {

    fun ratingsSortSlot(sort: UsersRatingsSort, onClick: () -> Unit): InventorySlot = InventorySlot.Builder {
        index = 50
        itemStack = config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                it.setDisplayName(
                    "${translation.sort}: ${sort.desc.toString(translation)}"
                )
            }
        }
        click = Click { onClick.invoke() }
    }

    fun playerRatingsSortSlot(sort: UserRatingsSort, click: Click) = InventorySlot.Builder {
        index = 50
        itemStack = config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                it.setDisplayName(
                    "${translation.sortRating}: ${sort.desc.toString(translation)}"
                )
            }
        }
        this.click = click
    }
}
