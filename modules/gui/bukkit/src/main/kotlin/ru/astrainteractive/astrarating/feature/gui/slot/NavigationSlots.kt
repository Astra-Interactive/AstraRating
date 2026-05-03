package ru.astrainteractive.astrarating.feature.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.feature.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.feature.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.feature.gui.util.TimeUtility
import ru.astrainteractive.astrarating.feature.gui.util.normalName
import ru.astrainteractive.astrarating.feature.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.feature.gui.util.toItemStack

internal fun SlotContext.backPageSlot(index: Int, click: Click) = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.back.toItemStack())
    .editMeta { displayName(translation.gui.menuClose.component) }
    .setOnClickListener(click)
    .build()

internal fun SlotContext.nextPageSlot(index: Int): InventorySlot = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.next.toItemStack())
    .editMeta { displayName(translation.gui.menuNextPage.component) }
    .setOnClickListener { (menu as PaginatedInventoryMenu).showNextPage() }
    .build()

internal fun SlotContext.prevPageSlot(index: Int): InventorySlot = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.prev.toItemStack())
    .editMeta { displayName(translation.gui.menuPrevPage.component) }
    .setOnClickListener { (menu as PaginatedInventoryMenu).showPrevPage() }
    .build()

internal fun SlotContext.ratingsPlayerSlot(
    i: Int,
    userAndRating: RatedUserDTO,
    onClick: () -> Unit
) = InventorySlot.Builder()
    .setIndex(i)
    .setItemStack(PlayerHeadUtil.getHead(userAndRating.userDTO.normalName))
    .editMeta {
        val color = if (userAndRating.ratingTotal > 0) translation.gui.positiveColor else translation.gui.negativeColor

        displayName(translation.gui.playerNameColor.plus(userAndRating.userDTO.normalName).component)
        buildList {
            if (config.gui.showFirstConnection) {
                val firstPlayerFormatted = TimeUtility.formatToString(
                    time = userAndRating.userDTO.offlinePlayer.firstPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (firstPlayerFormatted.isNotBlank() && userAndRating.userDTO.offlinePlayer.firstPlayed != 0L) {
                    add(translation.gui.firstConnection(firstPlayerFormatted))
                }
            }
            if (config.gui.showLastConnection) {
                val lastPlayedFormatted = TimeUtility.formatToString(
                    time = userAndRating.userDTO.offlinePlayer.lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                if (lastPlayedFormatted.isNotBlank() && userAndRating.userDTO.offlinePlayer.lastPlayed != 0L) {
                    add(translation.gui.lastConnection(lastPlayedFormatted))
                }
            }
            add(translation.gui.ratingTotal(color.plus("${userAndRating.ratingTotal}").raw))
        }
    }
    .setOnClickListener { onClick.invoke() }
    .build()
