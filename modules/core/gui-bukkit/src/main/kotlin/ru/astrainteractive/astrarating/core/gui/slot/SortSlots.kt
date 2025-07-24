package ru.astrainteractive.astrarating.core.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astrarating.core.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.core.gui.util.desc
import ru.astrainteractive.astrarating.core.gui.util.toItemStack
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort

internal fun SlotContext.ratingsSortSlot(
    sort: UsersRatingsSort,
    onClick: () -> Unit
): InventorySlot =
    InventorySlot.Builder()
        .setIndex(50)
        .setItemStack(config.gui.buttons.sort.toItemStack())
        .setDisplayName(translation.sort.component)
        .editMeta {
            translation.sort.plus(": ")
                .plus(sort.desc.toString(translation))
                .component
                .run(::displayName)
        }
        .setOnClickListener { onClick.invoke() }
        .build()

internal fun SlotContext.playerRatingsSortSlot(sort: UserRatingsSort, click: Click) = InventorySlot.Builder()
    .setIndex(50)
    .setItemStack(config.gui.buttons.sort.toItemStack())
    .editMeta {
        translation.sortRating
            .plus(": ")
            .plus(sort.desc.toString(translation))
            .component
            .run(::displayName)
    }
    .setOnClickListener(click)
    .build()
