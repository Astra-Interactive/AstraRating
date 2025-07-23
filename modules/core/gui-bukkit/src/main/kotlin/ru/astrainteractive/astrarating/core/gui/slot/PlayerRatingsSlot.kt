package ru.astrainteractive.astrarating.core.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astrarating.core.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.core.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.core.gui.util.TimeUtility
import ru.astrainteractive.astrarating.core.gui.util.subListFromString

@Suppress("LongParameterList")
internal fun SlotContext.playerRatingsSlot(
    index: Int,
    userCreatedReportName: String,
    color: StringDesc.Raw,
    message: String,
    firstPlayed: Long,
    lastPlayed: Long,
    canDelete: Boolean,
    click: Click
): InventorySlot = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(PlayerHeadUtil.getHead(userCreatedReportName))
    .editMeta {
        displayName(translation.playerNameColor.plus(userCreatedReportName).component)
        buildList {
            subListFromString(
                translation.message.plus(" ").plus(color).plus(message).raw,
                config.trimMessageAfter,
                config.cutWords
            ).forEachIndexed { _, messagePart -> add(color.plus(messagePart).component) }

            if (config.gui.showFirstConnection) {
                val time = TimeUtility.formatToString(
                    time = firstPlayed,
                    format = config.gui.format
                ).orEmpty()
                add(translation.firstConnection.plus(" ").plus(time).component)
            }
            if (config.gui.showLastConnection) {
                val time = TimeUtility.formatToString(
                    time = lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                add(translation.lastConnection.plus(" ").plus(time).component)
            }
            if (canDelete && config.gui.showDeleteReport) {
                add(translation.clickToDeleteReport.component)
            }
        }.run(::lore)
    }
    .setOnClickListener(click)
    .build()
