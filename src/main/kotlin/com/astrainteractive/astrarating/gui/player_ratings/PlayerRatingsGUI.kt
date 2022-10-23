package com.astrainteractive.astrarating.gui.player_ratings

import ru.astrainteractive.astralibs.events.EventManager
import ru.astrainteractive.astralibs.utils.editMeta
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.gui.ratings.RatingsGUIViewModel
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*

class PlayerRatingsGUI(val selectedPlayer: OfflinePlayer, player: Player) : PaginatedMenu() {
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val translation:PluginTranslation
        get() = TranslationProvider.value
    override val playerMenuUtility: IPlayerHolder = DefaultPlayerHolder(player)

    private val viewModel = PlayerRatingsGUIViewModel(selectedPlayer)
    val sortButtonIndex: Int = 50

    override var menuTitle: String = translation.playerRatingTitle.replace("%player%", selectedPlayer.name ?: "")
    override val menuSize: AstraMenuSize = AstraMenuSize.XL

    override val backPageButton = object : IInventoryButton {
        override val index: Int = 49
        override val item: ItemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }

    }
    override val nextPageButton = object : IInventoryButton {
        override val index: Int = 53
        override val item: ItemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }

    }
    override val prevPageButton = object : IInventoryButton {
        override val index: Int = 45
        override val item: ItemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }

    }


    private val sortButton: ItemStack
        get() = config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                it.setDisplayName("${translation.sortRating}: ${viewModel.sort.value.desc}")
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = 0

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        when (e.slot) {
            backPageButton.index -> PluginScope.launch(Dispatchers.IO) {
                RatingsGUI(playerMenuUtility.player).open()
            }

            sortButtonIndex -> {
                viewModel.onSortClicked()
                setMenuItems()
            }

            else -> {
                if (!AstraPermission.DeleteReport.hasPermission(playerMenuUtility.player)) return
                if (e.click != ClickType.LEFT) return
                val item = viewModel.userRatings.value[maxItemsPerPage * page + e.slot]
                viewModel.onDeleteClicked(item) {
                    setMenuItems()
                }

            }
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.onDisable()
    }

    override fun onPageChanged() {
        setMenuItems()
    }


    override fun onCreated() {
        viewModel.userRatings.collectOn {
            setMenuItems()
        }
        setMenuItems()
    }

    fun setMenuItems() {
        inventory.clear()
        setManageButtons()
        inventory.setItem(sortButtonIndex, sortButton)
        val list = viewModel.userRatings.value
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size)
                continue
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating > 0) translation.positiveColor else translation.negativeColor
            val item = RatingsGUIViewModel.getHead(userAndRating.userCreatedReport.normalName).apply {
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
                        if (AstraPermission.DeleteReport.hasPermission(playerMenuUtility.player) && config.gui.showDeleteReport)
                            add(translation.clickToDeleteReport)
                    }
                }
            }
            inventory.setItem(i, item)
        }

    }


}