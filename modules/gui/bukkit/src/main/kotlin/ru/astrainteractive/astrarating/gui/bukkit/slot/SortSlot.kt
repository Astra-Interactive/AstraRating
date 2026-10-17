package ru.astrainteractive.astrarating.gui.bukkit.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import ru.astrainteractive.astrarating.gui.api.rating.players.model.UsersRatingsSort
import ru.astrainteractive.astrarating.gui.bukkit.context.SlotContext
import ru.astrainteractive.astrarating.gui.bukkit.util.UserRatingsSortMapper
import ru.astrainteractive.astrarating.gui.bukkit.util.UsersRatingsSortMapper
import ru.astrainteractive.astrarating.gui.bukkit.util.toItemStack

internal fun SlotContext.ratingsSortSlot(
    index: Int,
    sortType: UserRatingsSort,
    userRatingsSortMapper: UserRatingsSortMapper,
    onClick: () -> Unit
): InventorySlot =
    InventorySlot.Builder()
        .setIndex(index = index)
        .setItemStack(config.gui.buttons.sort.toItemStack())
        .setDisplayName(translation.sort.title.toComponent(locale))
        .apply {
            listOf(
                UserRatingsSort.Rating(false),
                UserRatingsSort.Player(false),
                UserRatingsSort.Date(false),
            ).forEach { entry ->
                val option = translation.sort.option(
                    sort = userRatingsSortMapper.toText(entry),
                    isSelected = sortType::class == entry::class,
                    isAscending = sortType.isAsc
                )
                addLore(option.toComponent(locale))
            }
        }
        .setOnClickListener { onClick.invoke() }
        .build()

internal fun SlotContext.playerRatingsSortSlot(
    index: Int,
    sortType: UsersRatingsSort,
    usersRatingsSortMapper: UsersRatingsSortMapper,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.sort.toItemStack())
    .setDisplayName(translation.sort.title.toComponent(locale))
    .apply {
        listOf(
            UsersRatingsSort.TotalRating(false),
        ).forEach { entry ->
            val option = translation.sort.option(
                sort = usersRatingsSortMapper.toText(entry),
                isSelected = sortType::class == entry::class,
                isAscending = sortType.isAsc
            )
            addLore(option.toComponent(locale))
        }
    }
    .setOnClickListener(click)
    .build()
