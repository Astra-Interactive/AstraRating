package com.astrainteractive.astrarating.gui

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astrarating.utils.Translation
import com.astrainteractive.astrarating.utils.close
import com.astrainteractive.astrarating.utils.editMeta
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta


class SampleGUI(override val playerMenuUtility: AstraPlayerMenuUtility) : PaginatedMenu() {
    constructor(player: Player) : this(AstraPlayerMenuUtility(player))

    private val viewModel = SampleGUIViewModel()
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
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = 0


    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        if (e.slot == backButtonIndex)
            inventory.close()
    }


    /**
     * Handling current inventory closing
     */
    inner class CloseInventoryEventManager : EventManager {
        override val handlers: MutableList<EventListener> = mutableListOf()
        private val menuCloseHandler = DSLEvent.event(InventoryCloseEvent::class.java, this) {
            if (it.player != playerMenuUtility.player) return@event
            if (it.inventory.holder !is PaginatedMenu) return@event
            viewModel.onDisable()
            onDisable()
        }
    }

    private val innerClassHolder = CloseInventoryEventManager()

    override fun setMenuItems() {
        inventory.clear()
        addManageButtons()
    }


}