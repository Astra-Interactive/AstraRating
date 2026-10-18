package ru.astrainteractive.astrarating.gui.bukkit.slot

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astrarating.gui.bukkit.context.SlotContext
import ru.astrainteractive.astrarating.gui.bukkit.util.splitMessageLines
import ru.astrainteractive.astrarating.gui.bukkit.util.toHeadItemStack

@Suppress("LongParameterList")
internal fun SlotContext.playerRatingsSlot(
    index: Int,
    userCreatedReportName: String,
    isPositive: Boolean,
    message: String,
    ratedPlayer: OfflinePlayer,
    canDelete: Boolean,
    click: Click
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(Bukkit.getOfflinePlayer(userCreatedReportName).toHeadItemStack())
    .editMeta {
        displayName(translation.menu.playerName(userCreatedReportName).toComponent(locale))
        buildList {
            // The rating message is typed by a player, so it goes in as plain text, one lore line per part
            splitMessageLines(
                text = message,
                threshold = config.trimMessageAfter,
                cutWords = config.cutWords
            ).forEachIndexed { lineIndex, messagePart ->
                val line = translation.menu.ratingValue(isPositive = isPositive, text = messagePart)
                val labeledLine = if (lineIndex == 0) translation.playerRatingsMenu.message(line) else line
                add(labeledLine.toComponent(locale))
            }
            addAll(connectionLines(ratedPlayer))
            if (canDelete && config.gui.showDeleteReport) {
                add(translation.playerRatingsMenu.clickToDelete.toComponent(locale))
            }
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
