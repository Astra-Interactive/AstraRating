package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.gui.player_ratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.utils.Config
import com.astrainteractive.astrarating.utils.TimeUtility
import com.astrainteractive.astrarating.utils.Translation
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astralibs.utils.close
import ru.astrainteractive.astralibs.utils.editMeta
import java.util.*


class RatingsGUI(player: Player) : PaginatedMenu() {
    override val playerMenuUtility: IPlayerHolder = DefaultPlayerHolder(player)

    private val viewModel = RatingsGUIViewModel()
    val sortButtonIndex: Int = 50

    override var menuTitle: String = Translation.ratingsTitle
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton = object : IInventoryButton {
        override val index: Int = 49
        override val item: ItemStack = Config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(Translation.menuClose)
            }
        }

    }
    override val nextPageButton = object : IInventoryButton {
        override val index: Int = 53
        override val item: ItemStack = Config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(Translation.menuNextPage)
            }
        }

    }
    override val prevPageButton = object : IInventoryButton {
        override val index: Int = 45
        override val item: ItemStack = Config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(Translation.menuPrevPage)
            }
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


    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        when (e.slot) {
            backPageButton.index -> inventory.close()
            sortButtonIndex -> {
                viewModel.onSortClicked()
                setMenuItems()
            }

            else -> PluginScope.launch {
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
            val color = if (userAndRating.rating.rating > 0) Translation.positiveColor else Translation.negativeColor
            val item = RatingsGUIViewModel.getHead(userAndRating.reportedPlayer.normalName).apply {
                editMeta {
                    it.setDisplayName(Translation.playerNameColor + userAndRating.reportedPlayer.normalName)
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