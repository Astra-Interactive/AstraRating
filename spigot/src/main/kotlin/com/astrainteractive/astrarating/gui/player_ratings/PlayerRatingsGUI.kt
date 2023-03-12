package com.astrainteractive.astrarating.gui.player_ratings

import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.gui.ratings.RatingsGUIViewModel
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.utils.InventoryButton
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import ru.astrainteractive.astralibs.menu.utils.MenuSize
import ru.astrainteractive.astralibs.menu.utils.click.MenuClickListener

class PlayerRatingsGUI(val selectedPlayer: OfflinePlayer, player: Player) : PaginatedMenu() {
    private val clickListener = MenuClickListener()
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val translation: PluginTranslation
        get() = TranslationProvider.value
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    private val viewModel = PlayerRatingsGUIViewModel(selectedPlayer)

    override var menuTitle: String = translation.playerRatingTitle.replace("%player%", selectedPlayer.name ?: "")
    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton = ItemStackButtonBuilder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        onClick = {
            PluginScope.launch(Dispatchers.IO) {
                RatingsGUI(playerHolder.player).open()
            }
        }

    }
    override val nextPageButton = ItemStackButtonBuilder {
        index = 53
        itemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }
    }
    override val prevPageButton = ItemStackButtonBuilder {
        index = 45
        itemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }
    }


    private val sortButton: InventoryButton
        get() = ItemStackButtonBuilder {
            index = 50
            itemStack = config.gui.buttons.sort.toItemStack().apply {
                editMeta {
                    it.setDisplayName("${translation.sortRating}: ${viewModel.sort.value.desc}")
                }
            }
            onClick = {
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
        viewModel.userRatings.collectOn {
            setMenuItems()
        }
    }

    fun setMenuItems() {
        inventory.clear()
        setManageButtons(clickListener)
        sortButton.also(clickListener::remember).setInventoryButton()
        val list = viewModel.userRatings.value
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size)
                continue
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating > 0) translation.positiveColor else translation.negativeColor
            ItemStackButtonBuilder {
                this.index = i
                itemStack = RatingsGUIViewModel.getHead(userAndRating.userCreatedReport.normalName).apply {
                    editMeta {
                        it.setDisplayName(translation.playerNameColor + userAndRating.userCreatedReport.normalName)
                        it.lore = mutableListOf<String>().apply {
                            subListFromString(
                                "${translation.message} $color${userAndRating.rating.message}",
                                config.trimMessageAfter
                            ).forEachIndexed { index, it ->
                                add("$color$it")
                            }

                            if (config.gui.showFirstConnection)
                                add("${translation.firstConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.firstPlayed)}")
                            if (config.gui.showLastConnection)
                                add("${translation.lastConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.lastPlayed)}")
                            if (AstraPermission.DeleteReport.hasPermission(playerHolder.player) && config.gui.showDeleteReport)
                                add(translation.clickToDeleteReport)
                        }
                    }
                }
                onClick = onClick@{ e ->
                    if (!AstraPermission.DeleteReport.hasPermission(playerHolder.player)) return@onClick
                    if (e.click != ClickType.LEFT) return@onClick
                    val item = viewModel.userRatings.value.getOrNull(maxItemsPerPage * page + e.slot) ?: return@onClick
                    viewModel.onDeleteClicked(item)
                }
            }.also(clickListener::remember).setInventoryButton()
        }

    }


}