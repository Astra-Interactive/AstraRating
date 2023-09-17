package com.astrainteractive.astrarating.gui.playerratings

import com.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiModule
import com.astrainteractive.astrarating.gui.ratings.RatingsGUIViewModel
import com.astrainteractive.astrarating.plugin.AstraPermission
import com.astrainteractive.astrarating.utils.TimeUtility
import com.astrainteractive.astrarating.utils.desc
import com.astrainteractive.astrarating.utils.normalName
import com.astrainteractive.astrarating.utils.offlinePlayer
import com.astrainteractive.astrarating.utils.subListFromString
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu

class PlayerRatingsGUI(
    selectedPlayer: OfflinePlayer,
    player: Player,
    private val module: PlayerRatingGuiModule
) : PaginatedMenu(), PlayerRatingGuiModule by module {

    private val clickListener = MenuClickListener()
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    private val viewModel = PlayerRatingsGUIViewModel(selectedPlayer, module)

    override var menuTitle: String = translation.playerRatingTitle.replace("%player%", selectedPlayer.name ?: "")
    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton = InventorySlot.Builder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        click = Click {
            componentScope.launch(dispatchers.BukkitAsync) {
                val inventory = module.playerRatingsGuiFactory(playerHolder.player).create()
                withContext(dispatchers.BukkitMain) {
                    inventory.open()
                }
            }
        }
    }
    override val nextPageButton = InventorySlot.Builder {
        index = 53
        itemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }
    }
    override val prevPageButton = InventorySlot.Builder {
        index = 45
        itemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }
    }

    private val sortButton: InventorySlot
        get() = InventorySlot.Builder {
            index = 50
            itemStack = config.gui.buttons.sort.toItemStack().apply {
                editMeta {
                    it.setDisplayName("${translation.sortRating}: ${viewModel.sort.value.desc}")
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
        when (e.slot) {
            else -> {
            }
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        viewModel.userRatings.collectOn(dispatchers.BukkitMain) {
            setMenuItems()
        }
    }

    private fun setMenuItems() {
        inventory.clear()
        setManageButtons(clickListener)
        sortButton.also(clickListener::remember).setInventoryButton()
        val list = viewModel.userRatings.value
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size) {
                continue
            }
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating > 0) translation.positiveColor else translation.negativeColor
            InventorySlot.Builder {
                this.index = i
                itemStack = RatingsGUIViewModel.getHead(userAndRating.userCreatedReport.normalName).apply {
                    editMeta {
                        it.setDisplayName(translation.playerNameColor + userAndRating.userCreatedReport.normalName)
                        it.lore = mutableListOf<String>().apply {
                            subListFromString(
                                "${translation.message} $color${userAndRating.rating.message}",
                                config.trimMessageAfter
                            ).forEachIndexed { _, messagePart ->
                                add("$color$messagePart")
                            }

                            if (config.gui.showFirstConnection) {
                                val firstConnection = translation.firstConnection
                                val time = TimeUtility.formatToString(
                                    time = userAndRating.reportedPlayer.offlinePlayer.firstPlayed
                                )
                                add("$firstConnection $time")
                            }
                            if (config.gui.showLastConnection) {
                                val lastConnection = translation.lastConnection
                                val time = TimeUtility.formatToString(
                                    time = userAndRating.reportedPlayer.offlinePlayer.lastPlayed
                                )
                                add("$lastConnection $time")
                            }
                            if (AstraPermission.DeleteReport.hasPermission(playerHolder.player) &&
                                config.gui.showDeleteReport
                            ) {
                                add(translation.clickToDeleteReport)
                            }
                        }
                    }
                }
                click = Click { e ->
                    if (!AstraPermission.DeleteReport.hasPermission(playerHolder.player)) return@Click
                    if (e.click != ClickType.LEFT) return@Click
                    val item = viewModel.userRatings.value.getOrNull(maxItemsPerPage * page + e.slot) ?: return@Click
                    viewModel.onDeleteClicked(item)
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }
}
