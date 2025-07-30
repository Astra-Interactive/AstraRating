package ru.astrainteractive.astrarating.core.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.or
import ru.astrainteractive.astralibs.string.orEmpty
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astrarating.core.gui.mapping.UserRatingsSortMapper
import ru.astrainteractive.astrarating.core.gui.mapping.UsersRatingsSortMapper
import ru.astrainteractive.astrarating.core.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.core.gui.util.toItemStack
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort

internal fun SlotContext.ratingsSortSlot(
    sortType: UsersRatingsSort,
    usersRatingsSortMapper: UsersRatingsSortMapper,
    onClick: () -> Unit
): InventorySlot =
    InventorySlot.Builder()
        .setIndex(50)
        .setItemStack(config.gui.buttons.sort.toItemStack())
        .setDisplayName(translation.gui.sort.component)
        .setDisplayName(translation.gui.sortRating.component)
        .apply {
            listOf(
                UsersRatingsSort.Players(false),
            ).forEach { entry ->
                val symbol = translation.gui.sortAscSymbol
                    .takeIf { sortType.isAsc }
                    .or { translation.gui.sortDescSymbol }
                    .takeIf { sortType::class == entry::class }
                    .orEmpty()

                addLore(
                    translation.gui.enabledColor
                        .takeIf { sortType::class == entry::class }
                        .or { translation.gui.disabledColor }
                        .plus(usersRatingsSortMapper.toStringDesc(entry))
                        .plus(symbol)
                        .component
                )
            }
        }
        .setOnClickListener { onClick.invoke() }
        .build()

internal fun SlotContext.playerRatingsSortSlot(
    sortType: UserRatingsSort,
    userRatingsSortMapper: UserRatingsSortMapper,
    click: Click
) = InventorySlot.Builder()
    .setIndex(50)
    .setItemStack(config.gui.buttons.sort.toItemStack())
    .setDisplayName(translation.gui.sortRating.component)
    .apply {
        listOf(
            UserRatingsSort.Player(false),
            UserRatingsSort.Date(false),
            UserRatingsSort.Rating(false),
        ).forEach { entry ->
            val symbol = translation.gui.sortAscSymbol
                .takeIf { sortType.isAsc }
                .or { translation.gui.sortDescSymbol }
                .takeIf { sortType::class == entry::class }
                .orEmpty()

            addLore(
                translation.gui.enabledColor
                    .takeIf { sortType::class == entry::class }
                    .or { translation.gui.disabledColor }
                    .plus(userRatingsSortMapper.toStringDesc(entry))
                    .plus(symbol)
                    .component
            )
        }
    }
    .setOnClickListener(click)
    .build()
