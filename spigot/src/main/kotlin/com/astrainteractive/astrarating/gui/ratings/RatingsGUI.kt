package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.dto.UserAndRating
import com.astrainteractive.astrarating.gui.ratings.di.RatingsGUIModule
import com.astrainteractive.astrarating.utils.TimeUtility
import com.astrainteractive.astrarating.utils.desc
import com.astrainteractive.astrarating.utils.normalName
import com.astrainteractive.astrarating.utils.offlinePlayer
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
import java.util.*

class RatingsGUI(
    player: Player,
    private val module: RatingsGUIModule,
) : PaginatedMenu(), RatingsGUIModule by module {

    private val viewModel = RatingsGUIViewModel(module)

    val clickListener = MenuClickListener()

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
                    it.setDisplayName("${translation.sort}: ${viewModel.sort.value.desc}")
                }
            }
            click = Click {

                viewModel.onSortClicked()
                setMenuItems()
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = viewModel.userRatings.value.size

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        viewModel.userRatings.collectOn(Dispatchers.IO) { setMenuItems() }
    }

    fun setMenuItems(list: List<UserAndRating> = viewModel.userRatings.value) {
        inventory.clear()
        setManageButtons(clickListener)
//        backPageButton.also(clickListener::remember).setInventoryButton()
//        nextPageButton.also(clickListener::remember).setInventoryButton()
//        prevPageButton.also(clickListener::remember).setInventoryButton()

        sortButton.also(clickListener::remember).setInventoryButton()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size) {
                continue
            }
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating > 0) translation.positiveColor else translation.negativeColor
            InventorySlot.Builder {
                this.index = i
                itemStack = RatingsGUIViewModel.getHead(userAndRating.reportedPlayer.normalName).apply {
                    editMeta {
                        it.setDisplayName(translation.playerNameColor + userAndRating.reportedPlayer.normalName)
                        it.lore = mutableListOf<String>().apply {
                            if (config.gui.showFirstConnection) {
                                add(
                                    "${translation.firstConnection} ${TimeUtility.formatToString(
                                        userAndRating.reportedPlayer.offlinePlayer.firstPlayed
                                    )}"
                                )
                            }
                            if (config.gui.showLastConnection) {
                                add(
                                    "${translation.lastConnection} ${TimeUtility.formatToString(
                                        userAndRating.reportedPlayer.offlinePlayer.lastPlayed
                                    )}"
                                )
                            }
                            add("${translation.rating}: ${color}${userAndRating.rating.rating}")
                        }
                    }
                }
                click = Click {
                    componentScope.launch(dispatchers.BukkitAsync) {
                        val inventory = module.playerRatingsGuiFactory(
                            Bukkit.getOfflinePlayer(UUID.fromString(userAndRating.reportedPlayer.minecraftUUID)),
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
