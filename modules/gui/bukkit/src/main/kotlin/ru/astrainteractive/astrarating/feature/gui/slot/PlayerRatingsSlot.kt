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
import ru.astrainteractive.astrarating.feature.gui.util.subListFromString

@Suppress("LongParameterList")
internal fun SlotContext.playerRatingsSlot(
    index: Int,
    userCreatedReportName: String,
    isPositive: Boolean,
    message: String,
    firstPlayed: Long,
    lastPlayed: Long,
    canDelete: Boolean,
    click: Click
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(PlayerHeadUtil.getHead(userCreatedReportName))
    .editMeta {
        displayName(translation.menu.playerName(userCreatedReportName).toComponent(locale))
        buildList {
            // The rating message is typed by a player, so it goes in as plain text, one lore line per part
            subListFromString(
                message,
                config.trimMessageAfter,
                config.cutWords
            ).forEachIndexed { lineIndex, messagePart ->
                val line = translation.menu.ratingValue(isPositive = isPositive, text = messagePart)
                val labeledLine = if (lineIndex == 0) translation.playerRatingsMenu.message(line) else line
                add(labeledLine.toComponent(locale))
            }

            if (config.gui.showFirstConnection) {
                val time = TimeUtility.formatToString(
                    time = firstPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (time.isNotBlank() && firstPlayed != 0L) {
                    add(translation.menu.firstConnection(time).toComponent(locale))
                }
            }
            if (config.gui.showLastConnection) {
                val time = TimeUtility.formatToString(
                    time = lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (time.isNotBlank() && lastPlayed != 0L) {
                    add(translation.menu.lastConnection(time).toComponent(locale))
                }
            }
            if (canDelete && config.gui.showDeleteReport) {
                add(translation.playerRatingsMenu.clickToDelete.toComponent(locale))
            }
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
