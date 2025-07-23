package ru.astrainteractive.astrarating.gui.slot

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
import ru.astrainteractive.astrarating.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.gui.util.toItemStack

internal fun SlotContext.backPageSlot(click: Click) = InventorySlot.Builder()
    .setIndex(49)
    .setItemStack(config.gui.buttons.back.toItemStack())
    .editMeta { displayName(translation.menuClose.component) }
    .setOnClickListener(click)
    .build()

internal val SlotContext.nextPageSlot: InventorySlot
    get() = InventorySlot.Builder()
        .setIndex(53)
        .setItemStack(config.gui.buttons.next.toItemStack())
        .editMeta { displayName(translation.menuNextPage.component) }
        .setOnClickListener { (menu as PaginatedInventoryMenu).showNextPage() }
        .build()

internal val SlotContext.prevPageSlot: InventorySlot
    get() = InventorySlot.Builder()
        .setIndex(45)
        .setItemStack(config.gui.buttons.prev.toItemStack())
        .editMeta { displayName(translation.menuPrevPage.component) }
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
        val color = if (userAndRating.ratingTotal > 0) translation.positiveColor else translation.negativeColor

        displayName(translation.playerNameColor.plus(userAndRating.userDTO.normalName).component)
        buildList {
            if (config.gui.showFirstConnection) {
                val firstPlayerFormatted = TimeUtility.formatToString(
                    time = userAndRating.userDTO.offlinePlayer.firstPlayed,
                    format = config.gui.format
                ).orEmpty()

                add(translation.firstConnection.plus(" ").plus(firstPlayerFormatted))
            }
            if (config.gui.showLastConnection) {
                val lastPlayedFormatted = TimeUtility.formatToString(
                    time = userAndRating.userDTO.offlinePlayer.lastPlayed,
                    format = config.gui.format
                ).orEmpty()
                add(translation.lastConnection.plus(" ").plus(lastPlayedFormatted))
            }
            add(translation.ratingTotal.plus(": ").plus(color).plus("${userAndRating.ratingTotal}"))
        }
    }
    .setOnClickListener { onClick.invoke() }
    .build()
