@file:Suppress("ExpressionBodyOnOneLine")

package ru.astrainteractive.astrarating.gui.bukkit.slot

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astrarating.gui.bukkit.context.SlotContext
import ru.astrainteractive.astrarating.gui.bukkit.util.toHeadItemStack

@Suppress("LongParameterList")
internal fun SlotContext.ratingsSlot(
    index: Int,
    click: Click,
    player: OfflinePlayer,
    ratingTotal: Int,
    ratingCounts: Long,
    playerName: String
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(player.toHeadItemStack())
    .editMeta {
        displayName(translation.menu.playerName(playerName).toComponent(locale))
        buildList {
            addAll(connectionLines(player))
            val rating = translation.menu.ratingValue(isPositive = ratingTotal > 0, text = "$ratingTotal")
            add(translation.ratingsMenu.total(rating).toComponent(locale))
            add(translation.ratingsMenu.count(ratingCounts).toComponent(locale))
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
