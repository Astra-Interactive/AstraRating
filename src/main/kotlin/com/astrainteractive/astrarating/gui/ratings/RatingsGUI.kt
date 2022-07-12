package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astrarating.gui.player_ratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class RatingsGUI(override val playerMenuUtility: AstraPlayerMenuUtility) : PaginatedMenu() {
    constructor(player: Player) : this(AstraPlayerMenuUtility(player))

    private val viewModel = RatingsGUIViewModel()
    val sortButtonIndex: Int = 50
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53
    override val prevButtonIndex: Int = 45

    override var menuName: String = Translation.ratingsTitle
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = Config.gui.buttons.back.toItemStack().apply {
        editMeta {
            it.setDisplayName(Translation.menuClose)
        }
    }
    override val nextPageButton: ItemStack = Config.gui.buttons.next.toItemStack().apply {
        editMeta {
            it.setDisplayName(Translation.menuNextPage)
        }
    }
    override val prevPageButton: ItemStack = Config.gui.buttons.prev.toItemStack().apply {
        editMeta {
            it.setDisplayName(Translation.menuPrevPage)
        }
    }
    private val sortButton: ItemStack
        get() = Config.gui.buttons.sort.toItemStack().apply {
            editMeta {
                it.setDisplayName("${Translation.sort}: ${viewModel.sort.value.desc}")
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = 0


    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when (e.slot) {
            backButtonIndex -> inventory.close()
            sortButtonIndex -> {
                viewModel.onSortClicked()
                setMenuItems()
            }
            else -> AsyncHelper.launch {
                val item = viewModel.userRatings.value[maxItemsPerPage * page + e.slot]
                PlayerRatingsGUI(Bukkit.getOfflinePlayer(item.reportedPlayer.minecraftName), playerMenuUtility).open()
            }
        }
    }


    /**
     * Handling current inventory closing
     */
    inner class CloseInventoryEventManager : EventManager {
        override val handlers: MutableList<EventListener> = mutableListOf()
        private val menuCloseHandler = DSLEvent.event(InventoryCloseEvent::class.java, this) {
            if (it.player != playerMenuUtility.player) return@event
            if (it.inventory.holder !is RatingsGUI) return@event
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
            val color = if (userAndRating.rating.rating > 0) Translation.positiveColor else Translation.negativeColor
            val item = RatingsGUIViewModel.getHead(userAndRating.reportedPlayer.minecraftName).apply {
                editMeta {
                    it.setDisplayName(Translation.playerNameColor + userAndRating.reportedPlayer.minecraftName)
                    it.lore = mutableListOf<String>().apply {
                        if (Config.gui.showFirstConnection)
                            add("${Translation.firstConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.firstPlayed)}")
                        if (Config.gui.showLastConnection)
                            add("${Translation.lastConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.lastPlayed)}")
                        add("${Translation.rating}: ${color}${userAndRating.rating.rating}")
                    }
                }
            }
            inventory.setItem(i, item)
        }

    }


}