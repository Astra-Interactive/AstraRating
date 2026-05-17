package ru.astrainteractive.astrarating.feature.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
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

internal fun SlotContext.nextPageSlot(index: Int, click: Click): InventorySlot = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.next.toItemStack())
    .editMeta { displayName(translation.gui.menuNextPage.component) }
    .setOnClickListener(click)
    .build()

internal fun SlotContext.prevPageSlot(index: Int, click: Click): InventorySlot = InventorySlot.Builder()
    .setIndex(index = index)
    .setItemStack(config.gui.buttons.prev.toItemStack())
    .editMeta { displayName(translation.gui.menuPrevPage.component) }
    .setOnClickListener(click)
    .build()
