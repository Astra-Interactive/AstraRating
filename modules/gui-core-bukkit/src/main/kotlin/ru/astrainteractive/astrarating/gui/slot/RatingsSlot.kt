package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrarating.core.util.StringDescExt.plus
import ru.astrainteractive.astrarating.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility

@Suppress("LongParameterList")
internal fun SlotContext.ratingsSlot(
    index: Int,
    click: Click,
    firstPlayed: Long,
    lastPlayed: Long,
    rating: Int,
    playerName: String
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(PlayerHeadUtil.getHead(playerName))
    .editMeta {
        val color = when {
            rating > 0 -> translation.positiveColor.raw
            else -> translation.negativeColor.raw
        }
        displayName(translation.playerNameColor.plus(playerName).component)
        buildList {
            if (config.gui.showFirstConnection) {
                val timeFormatted = TimeUtility.formatToString(
                    time = firstPlayed,
                    format = config.gui.format
                ).orEmpty()
                add(translation.firstConnection.plus(" ").plus(timeFormatted).component)
            }
            if (config.gui.showLastConnection) {
                val timeFormatted = TimeUtility.formatToString(
                    time = lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                add(translation.lastConnection.plus(" ").plus(timeFormatted).component)
            }
            translation.rating.plus(": ").plus(color).plus("$rating").component.run(::add)
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
