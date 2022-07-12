package com.astrainteractive.astrarating.gui.player_ratings

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.gui.ratings.RatingsGUIViewModel
import com.astrainteractive.astrarating.utils.AstraPermission
import com.astrainteractive.astrarating.utils.Translation
import com.astrainteractive.astrarating.utils.editMeta
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class PlayerRatingsGUI(val selectedPlayer: OfflinePlayer, override val playerMenuUtility: AstraPlayerMenuUtility) :
    PaginatedMenu() {

    private val viewModel = PlayerRatingsGUIViewModel(selectedPlayer)
    val sortButtonIndex: Int = 50
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53
    override val prevButtonIndex: Int = 45

    override var menuName: String = Translation.playerRatingTitle.replace("%player%",selectedPlayer.name?:"")
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = ItemStack(Material.PAPER).apply {
        editMeta {
            it.setDisplayName(Translation.menuBack)
        }
    }
    override val nextPageButton: ItemStack = ItemStack(Material.PAPER).apply {
        editMeta {
            it.setDisplayName(Translation.menuNextPage)
        }
    }
    override val prevPageButton: ItemStack = ItemStack(Material.PAPER).apply {
        editMeta {
            it.setDisplayName(Translation.menuPrevPage)
        }
    }
    private val sortButton: ItemStack
        get() = ItemStack(Material.SUNFLOWER).apply {
            editMeta {
                it.setDisplayName("${Translation.sortRating}: ${viewModel.sort.value.desc}")
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = 0


    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        if (e.slot == backButtonIndex) AsyncHelper.launch {
            RatingsGUI(playerMenuUtility).open()
        }
        else if (e.slot == sortButtonIndex) {
            viewModel.onSortClicked()
            setMenuItems()
        } else {
            if (!AstraPermission.DeleteReport.hasPermission(playerMenuUtility.player)) return
            if (e.click != ClickType.LEFT) return
            val item = viewModel.userRatings.value[maxItemsPerPage * page + e.slot]
            viewModel.onDeleteClicked(item)

        }
    }


    /**
     * Handling current inventory closing
     */
    inner class CloseInventoryEventManager : EventManager {
        override val handlers: MutableList<EventListener> = mutableListOf()
        private val menuCloseHandler = DSLEvent.event(InventoryCloseEvent::class.java, this) {
            if (it.player != playerMenuUtility.player) return@event
            if (it.inventory.holder !is PlayerRatingsGUI) return@event
            viewModel.onDisable()
            stateFlowHolder.cancel()
            onDisable()
        }
    }

    private val innerClassHolder = CloseInventoryEventManager()
    private val stateFlowHolder = AsyncHelper.launch {
        viewModel.userRatings.collectLatest {
            setMenuItems()
        }
    }

    override fun setMenuItems() {
        inventory.clear()
        addManageButtons()
        inventory.setItem(sortButtonIndex, sortButton)
        val list = viewModel.userRatings.value
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size)
                continue
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating>0) Translation.positiveColor else Translation.negativeColor
            val item = RatingsGUIViewModel.getHead(userAndRating.userCreatedReport.minecraftName).apply {
                editMeta {
                    it.setDisplayName(Translation.playerNameColor + userAndRating.userCreatedReport.minecraftName)
                    it.lore = mutableListOf(
//                        "${Translation.rating}: $color${userAndRating.rating.rating}",
                        "${Translation.message}: $color${userAndRating.rating.message}",
                    ).apply {
                        if (AstraPermission.DeleteReport.hasPermission(playerMenuUtility.player))
                            add(Translation.clickToDeleteReport)
                    }
                }
            }
            inventory.setItem(i, item)
        }

    }


}