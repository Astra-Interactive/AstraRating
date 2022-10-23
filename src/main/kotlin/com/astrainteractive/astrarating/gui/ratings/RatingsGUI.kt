package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.gui.player_ratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.PluginTranslation
import com.astrainteractive.astrarating.utils.TimeUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astralibs.utils.close
import ru.astrainteractive.astralibs.utils.editMeta
import java.util.*


class RatingsGUI(player: Player) : PaginatedMenu() {
    override val playerMenuUtility: IPlayerHolder = DefaultPlayerHolder(player)
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val translation:PluginTranslation
        get() = TranslationProvider.value

    private val viewModel = RatingsGUIViewModel()
    val sortButtonIndex: Int = 50

    override var menuTitle: String = translation.ratingsTitle
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
                it.setDisplayName("${translation.sort}: ${viewModel.sort.value.desc}")
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
            backPageButton.index -> inventory.close()
            sortButtonIndex -> {
                viewModel.onSortClicked()
                setMenuItems()
            }

            else -> PluginScope.launch(Dispatchers.IO) {
                val item = viewModel.userRatings.value[maxItemsPerPage * page + e.slot]
                PlayerRatingsGUI(
                    Bukkit.getOfflinePlayer(UUID.fromString(item.reportedPlayer.minecraftUUID)),
                    playerMenuUtility.player
                ).open()
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
        println("RatingsGUI ${this as InventoryHolder}")
        viewModel.userRatings.collectOn { setMenuItems() }
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
            val item = RatingsGUIViewModel.getHead(userAndRating.reportedPlayer.normalName).apply {
                editMeta {
                    it.setDisplayName(translation.playerNameColor + userAndRating.reportedPlayer.normalName)
                    it.lore = mutableListOf<String>().apply {
                        if (config.gui.showFirstConnection)
                            add("${translation.firstConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.firstPlayed)}")
                        if (config.gui.showLastConnection)
                            add("${translation.lastConnection} ${TimeUtility.formatToString(userAndRating.reportedPlayer.offlinePlayer.lastPlayed)}")
                        add("${translation.rating}: ${color}${userAndRating.rating.rating}")
                    }
                }
            }
            inventory.setItem(i, item)
        }

    }


}