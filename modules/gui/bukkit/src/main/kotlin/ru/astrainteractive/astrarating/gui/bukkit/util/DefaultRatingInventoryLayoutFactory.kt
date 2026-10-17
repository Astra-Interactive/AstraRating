package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astralibs.menu.layout.slotInventoryLayout
import ru.astrainteractive.astrarating.gui.bukkit.model.RatingSlotKey

@Suppress("MagicNumber")
internal object DefaultRatingInventoryLayoutFactory {

    private fun createDefaultLayout() = slotInventoryLayout {
        repeat(5) {
            row(9, RatingSlotKey.RATING_ITEM)
        }
        row(
            RatingSlotKey.PREV_PAGE,
            RatingSlotKey.EVENT,
            RatingSlotKey.EMPTY,
            RatingSlotKey.EMPTY,
            RatingSlotKey.BACK,
            RatingSlotKey.SORT,
            RatingSlotKey.EMPTY,
            RatingSlotKey.EMPTY,
            RatingSlotKey.NEXT_PAGE
        )
    }

    fun create() = createDefaultLayout()
}
