package ru.astrainteractive.astrarating.gui.ratings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.ratings.di.RatingsGUIModule
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.util.TimeUtility
import ru.astrainteractive.astrarating.util.desc
import ru.astrainteractive.astrarating.util.normalName
import ru.astrainteractive.astrarating.util.offlinePlayer
import ru.astrainteractive.astrarating.util.toItemStack
import java.util.*

class RatingsGUI(
    player: Player,
    private val module: RatingsGUIModule,
    private val allRatingsComponent: AllRatingsComponent
) : PaginatedMenu(), RatingsGUIModule by module {
    private val loadingIndicator = LoadingIndicator(menu = this, translation = translation)

    private val clickListener = MenuClickListener()

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var menuTitle: String = translation.ratingsTitle
    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton = InventorySlot.Builder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        click = Click { inventory.close() }
    }
    override val nextPageButton = InventorySlot.Builder {
        index = 53
        itemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }
        click = Click { showPage(page + 1) }
    }
    override val prevPageButton = InventorySlot.Builder {
        index = 45
        itemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }
        click = Click { showPage(page - 1) }
    }
    private val sortButton: InventorySlot
        get() = InventorySlot.Builder {
            index = 50
            itemStack = config.gui.buttons.sort.toItemStack().apply {
                editMeta {
                    it.setDisplayName(
                        "${translation.sort}: ${
                        allRatingsComponent.model.value.sort.desc.toString(
                            translation
                        )
                        }"
                    )
                }
            }
            click = Click {
                allRatingsComponent.onSortClicked()
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = allRatingsComponent.model.value.userRatings.size

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        allRatingsComponent.close()
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        allRatingsComponent.model.collectOn(Dispatchers.IO) {
            if (it.isLoading) {
                inventory.clear()
                setManageButtons(clickListener)
                loadingIndicator.display()
            } else {
                loadingIndicator.stop()
                setMenuItems()
            }
        }
    }

    fun setMenuItems(model: AllRatingsComponent.Model = allRatingsComponent.model.value) {
        inventory.clear()
        setManageButtons(clickListener)
        sortButton.also(clickListener::remember).setInventoryButton()

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= model.userRatings.size) {
                continue
            }
            val userAndRating = model.userRatings[index]
            val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
            InventorySlot.Builder {
                this.index = i
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
                click = Click {
                    componentScope.launch(dispatchers.BukkitAsync) {
                        val inventory = module.allRatingsGuiFactory(
                            Bukkit.getOfflinePlayer(UUID.fromString(userAndRating.userDTO.minecraftUUID)),
                            playerHolder.player,
                        ).create()
                        withContext(dispatchers.BukkitMain) {
                            inventory.open()
                        }
                    }
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }
}
