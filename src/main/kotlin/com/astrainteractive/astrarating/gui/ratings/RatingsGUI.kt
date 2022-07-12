package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astrarating.gui.player_ratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.utils.Translation
import com.astrainteractive.astrarating.utils.close
import com.astrainteractive.astrarating.utils.editMeta
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

    override var menuName: String = Translation.menuTitle
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = ItemStack(Material.PAPER).apply {
        editMeta {
            it.setDisplayName(Translation.menuClose)
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
                it.setDisplayName("${Translation.sort}: ${viewModel.sort.value.desc}")
            }
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = 0


    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        if (e.slot == backButtonIndex)
            inventory.close()
        else if (e.slot == sortButtonIndex) {
            viewModel.onSortClicked()
            setMenuItems()
        } else AsyncHelper.launch {
            val item = viewModel.userRatings.value[maxItemsPerPage * page * e.slot]
            PlayerRatingsGUI(Bukkit.getOfflinePlayer(item.userCreatedReport.minecraftName), playerMenuUtility).open()
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
            val item = RatingsGUIViewModel.getHead(userAndRating.userCreatedReport.minecraftName).apply {
                editMeta {
                    it.setDisplayName(userAndRating.userCreatedReport.minecraftName)
                    it.lore = listOf(
                        "${Translation.rating}: ${userAndRating.rating.rating}"
                    )
                }
            }
            inventory.setItem(i, item)
        }

    }


}