package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.gui.util.toItemStack

internal class NavigationSlots(
    slotContext: SlotContext,
    private val menu: PaginatedInventoryMenu,
    private val translationContext: KyoriComponentSerializer
) : SlotContext by slotContext {
    fun backPageSlot(click: Click) = InventorySlot.Builder()
        .setIndex(49)
        .setItemStack(config.gui.buttons.back.toItemStack())
        .editMeta {
            val component = translationContext.toComponent(translation.menuClose)
            displayName(component)
        }
        .setOnClickListener(click)
        .build()

    val nextPageSlot: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(53)
            .setItemStack(config.gui.buttons.next.toItemStack())
            .editMeta {
                val component = translationContext.toComponent(translation.menuNextPage)
                displayName(component)
            }
            .setOnClickListener { menu.showNextPage() }
            .build()

    val prevPageSlot: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(45)
            .setItemStack(config.gui.buttons.prev.toItemStack())
            .editMeta {
                val component = translationContext.toComponent(translation.menuPrevPage)
                displayName(component)
            }
            .setOnClickListener { menu.showPrevPage() }
            .build()

    fun ratingsPlayerSlot(
        i: Int,
        userAndRating: RatedUserDTO,
        onClick: () -> Unit
    ) = InventorySlot.Builder()
        .setIndex(i)
        .setItemStack(PlayerHeadUtil.getHead(userAndRating.userDTO.normalName))
        .editMeta {
            val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
            val component = translationContext.toComponent(
                translation.playerNameColor.raw + userAndRating.userDTO.normalName
            )
            displayName(component)
            buildList {
                if (config.gui.showFirstConnection) {
                    val component = translationContext.toComponent(
                        "${translation.firstConnection} ${
                            TimeUtility.formatToString(
                                time = userAndRating.userDTO.offlinePlayer.firstPlayed,
                                format = config.gui.format
                            )
                        }"
                    )
                    add(component)
                }
                if (config.gui.showLastConnection) {
                    val component = translationContext.toComponent(
                        "${translation.lastConnection} ${
                            TimeUtility.formatToString(
                                time = userAndRating.userDTO.offlinePlayer.lastPlayed,
                                format = config.gui.format
                            )
                        }"
                    )
                    add(component)
                }
                translationContext.toComponent("${translation.rating}: ${color}${userAndRating.rating}").run(::add)
            }
        }
        .setOnClickListener { onClick.invoke() }
        .build()
}
