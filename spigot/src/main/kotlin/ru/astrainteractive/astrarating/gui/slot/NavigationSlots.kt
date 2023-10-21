package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.gui.util.toItemStack
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class NavigationSlots(
    slotContext: SlotContext,
    private val menu: PaginatedMenu
) : SlotContext by slotContext {
    fun backPageSlot(click: Click) = InventorySlot.Builder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        this.click = click
    }

    val nextPageSlot by Provider {
        InventorySlot.Builder {
            index = 53
            itemStack = config.gui.buttons.next.toItemStack().apply {
                editMeta {
                    it.setDisplayName(translation.menuNextPage)
                }
            }
            click = Click { menu.showPage(menu.page + 1) }
        }
    }
    val prevPageSlot by Provider {
        InventorySlot.Builder {
            index = 45
            itemStack = config.gui.buttons.prev.toItemStack().apply {
                editMeta {
                    it.setDisplayName(translation.menuPrevPage)
                }
            }
            click = Click { menu.showPage(menu.page - 1) }
        }
    }

    fun ratingsPlayerSlot(
        i: Int,
        userAndRating: RatedUserDTO,
        onClick: () -> Unit
    ) = InventorySlot.Builder {
        this.index = i
        val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
        itemStack = PlayerHeadUtil.getHead(userAndRating.userDTO.normalName).apply {
            editMeta {
                it.setDisplayName(translation.playerNameColor + userAndRating.userDTO.normalName)
                it.lore = mutableListOf<String>().apply {
                    if (config.gui.showFirstConnection) {
                        add(
                            "${translation.firstConnection} ${
                            TimeUtility.formatToString(
                                time = userAndRating.userDTO.offlinePlayer.firstPlayed,
                                format = config.gui.format
                            )
                            }"
                        )
                    }
                    if (config.gui.showLastConnection) {
                        add(
                            "${translation.lastConnection} ${
                            TimeUtility.formatToString(
                                time = userAndRating.userDTO.offlinePlayer.lastPlayed,
                                format = config.gui.format
                            )
                            }"
                        )
                    }
                    add("${translation.rating}: ${color}${userAndRating.rating}")
                }
            }
        }
        click = Click { onClick.invoke() }
    }
}
