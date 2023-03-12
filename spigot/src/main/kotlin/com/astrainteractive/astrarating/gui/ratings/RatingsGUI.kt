package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.dto.UserAndRating
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
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.utils.InventoryButton
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import ru.astrainteractive.astralibs.menu.utils.MenuSize
import ru.astrainteractive.astralibs.menu.utils.click.MenuClickListener
import java.util.*


class RatingsGUI(player: Player) : PaginatedMenu() {
    val clickListener = MenuClickListener()
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val translation: PluginTranslation
        get() = TranslationProvider.value

    private val viewModel = RatingsGUIViewModel()


    override var menuTitle: String = translation.ratingsTitle
    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton = ItemStackButtonBuilder {
        index = 49
        itemStack = config.gui.buttons.back.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuClose)
            }
        }
        onClick = { inventory.close() }

    }
    override val nextPageButton = ItemStackButtonBuilder {
        index = 53
        itemStack = config.gui.buttons.next.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuNextPage)
            }
        }
        onClick = { showPage(page + 1) }

    }
    override val prevPageButton = ItemStackButtonBuilder {
        index = 45
        itemStack = config.gui.buttons.prev.toItemStack().apply {
            editMeta {
                it.setDisplayName(translation.menuPrevPage)
            }
        }
        onClick = { showPage(page - 1) }

    }
    private val sortButton: InventoryButton
        get() = ItemStackButtonBuilder {
            index = 50
            itemStack = config.gui.buttons.sort.toItemStack().apply {
                editMeta {
                    it.setDisplayName("${translation.sort}: ${viewModel.sort.value.desc}")
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

    fun setMenuItems(list: List<UserAndRating> = viewModel.userRatings.value) {
        inventory.clear()
        setManageButtons(clickListener)
//        backPageButton.also(clickListener::remember).setInventoryButton()
//        nextPageButton.also(clickListener::remember).setInventoryButton()
//        prevPageButton.also(clickListener::remember).setInventoryButton()

        sortButton.also(clickListener::remember).setInventoryButton()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size)
                continue
            val userAndRating = list[index]
            val color = if (userAndRating.rating.rating > 0) translation.positiveColor else translation.negativeColor
            ItemStackButtonBuilder {
                this.index = i
                itemStack = RatingsGUIViewModel.getHead(userAndRating.reportedPlayer.normalName).apply {
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
                onClick = {
                    PluginScope.launch(Dispatchers.IO) {
                        PlayerRatingsGUI(
                            Bukkit.getOfflinePlayer(UUID.fromString(userAndRating.reportedPlayer.minecraftUUID)),
                            playerHolder.player
                        ).open()
                    }
                }
            }.also(clickListener::remember).setInventoryButton()
        }

    }


}