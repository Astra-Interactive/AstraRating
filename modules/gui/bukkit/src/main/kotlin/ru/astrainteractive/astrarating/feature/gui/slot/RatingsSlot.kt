package ru.astrainteractive.astrarating.feature.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astrarating.feature.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.feature.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.feature.gui.util.TimeUtility

@Suppress("LongParameterList")
internal fun SlotContext.ratingsSlot(
    index: Int,
    click: Click,
    firstPlayed: Long,
    lastPlayed: Long,
    ratingTotal: Int,
    ratingCounts: Long,
    playerName: String
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(PlayerHeadUtil.getHead(playerName))
    .editMeta {
        displayName(translation.gui.playerName(playerName).toComponent(locale))
        buildList {
            if (config.gui.showFirstConnection) {
                val timeFormatted = TimeUtility.formatToString(
                    time = firstPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (timeFormatted.isNotBlank() && firstPlayed != 0L) {
                    add(translation.gui.firstConnection(timeFormatted).toComponent(locale))
                }
            }
            if (config.gui.showLastConnection) {
                val timeFormatted = TimeUtility.formatToString(
                    time = lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (timeFormatted.isNotBlank() && lastPlayed != 0L) {
                    add(translation.gui.lastConnection(timeFormatted).toComponent(locale))
                }
            }
            val rating = translation.gui.ratingValue(isPositive = ratingTotal > 0, text = "$ratingTotal")
            add(translation.gui.ratingTotal(rating).toComponent(locale))
            add(translation.gui.ratingCounts(ratingCounts).toComponent(locale))
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
