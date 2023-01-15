package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.domain.entities.tables.dto.UserAndRating
import com.astrainteractive.astrarating.gui.player_ratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*
import java.util.*


class RatingsGUI(player: Player) : PaginatedMenu() {
    override val playerMenuUtility: IPlayerHolder = PlayerHolder(player)
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val translation:PluginTranslation
        get() = TranslationProvider.value

    private val viewModel = RatingsGUIViewModel()
    val sortButtonIndex: Int = 50

    override var menuTitle: String = translation.ratingsTitle
    override val menuSize: MenuSize = MenuSize.XL
    override val backPageButton = object : IInventoryButton {
        override val index: Int = 49
        override val item: ItemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        override val onClick: (e: InventoryClickEvent) -> Unit = {}

    }
    override val nextPageButton = object : IInventoryButton {
        override val index: Int = 53
        override val item: ItemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }
        override val onClick: (e: InventoryClickEvent) -> Unit = {}

    }
    override val prevPageButton = object : IInventoryButton {
        override val index: Int = 45
        override val item: ItemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }
        override val onClick: (e: InventoryClickEvent) -> Unit = {}

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
        e.isCancelled = true
        handleChangePageClick(e.slot)
        when (e.slot) {
            backPageButton.index -> inventory.close()
            sortButtonIndex -> {
                viewModel.onSortClicked()
                setMenuItems()
            }

            else -> {
                val item = viewModel.userRatings.value.getOrNull(maxItemsPerPage * page + e.slot) ?: return
                PluginScope.launch(Dispatchers.IO) {
                    PlayerRatingsGUI(
                        Bukkit.getOfflinePlayer(UUID.fromString(item.reportedPlayer.minecraftUUID)),
                        playerMenuUtility.player
                    ).open()
                }
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
        viewModel.userRatings.collectOn(Dispatchers.IO) { setMenuItems() }
    }

    fun setMenuItems(list:List<UserAndRating> = viewModel.userRatings.value) {
        inventory.clear()
        setManageButtons()
        inventory.setItem(sortButtonIndex, sortButton)
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