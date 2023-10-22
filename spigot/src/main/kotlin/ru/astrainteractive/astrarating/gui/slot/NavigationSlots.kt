package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
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
    private val menu: PaginatedMenu,
    private val translationContext: KyoriComponentSerializer
) : SlotContext by slotContext {
    fun backPageSlot(click: Click) = InventorySlot.Builder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                val component = translationContext.toComponent(translation.menuClose)
                it.displayName(component)
            }
        }
        this.click = click
    }

    val nextPageSlot by Provider {
        InventorySlot.Builder {
            index = 53
            itemStack = config.gui.buttons.next.toItemStack().apply {
                editMeta {
                    val component = translationContext.toComponent(translation.menuNextPage)
                    it.displayName(component)
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
                    val component = translationContext.toComponent(translation.menuPrevPage)
                    it.displayName(component)
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
                val component = translationContext.toComponent(
                    translation.playerNameColor + userAndRating.userDTO.normalName
                )
                it.displayName(component)
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
        }
        click = Click { onClick.invoke() }
    }
}
