package ru.astrainteractive.astrarating.gui.playerratings

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrarating.feature.playerrating.PlayerRatingsComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiModule
import ru.astrainteractive.astrarating.gui.slot.NavigationSlots
import ru.astrainteractive.astrarating.gui.slot.SlotContext
import ru.astrainteractive.astrarating.gui.slot.SortSlots
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.gui.util.subListFromString
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.AstraPermission
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class PlayerRatingsGUI(
    selectedPlayer: OfflinePlayer,
    player: Player,
    private val module: PlayerRatingGuiModule,
    private val playerRatingsComponent: PlayerRatingsComponent
) : PaginatedMenu(), PlayerRatingGuiModule by module {

    private val loadingIndicator = LoadingIndicator(menu = this, translation = translation)

    private val clickListener = MenuClickListener()

    private val slotContext = object : SlotContext {
        override val translation: PluginTranslation = module.translation
        override val config: EmpireConfig = module.config
    }

    private val navigationSlots = NavigationSlots(
        slotContext = slotContext,
        menu = this
    )

    private val sortSlots = SortSlots(
        slotContext = slotContext,
        menu = this
    )

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var menuTitle: String = translation.playerRatingTitle.replace("%player%", selectedPlayer.name ?: "")

    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton by Provider {
        navigationSlots.backPageSlot {
            componentScope.launch(dispatchers.BukkitAsync) {
                val inventory = module.playerRatingsGuiFactory(playerHolder.player).create()
                withContext(dispatchers.BukkitMain) {
                    inventory.open()
                }
            }
        }
    }

    override val nextPageButton by Provider {
        navigationSlots.nextPageSlot
    }

    override val prevPageButton by Provider {
        navigationSlots.prevPageSlot
    }

    private val sortButton by Provider {
        sortSlots.playerRatingsSortSlot(
            sort = playerRatingsComponent.model.value.sort,
            click = {
                playerRatingsComponent.onSortClicked()
                setMenuItems()
            }
        )
    }

    override var maxItemsPerPage: Int = 45

    override var page: Int = 0

    override val maxItemsAmount: Int
        get() = playerRatingsComponent.model.value.userRatings.size

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        playerRatingsComponent.close()
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        playerRatingsComponent.model.collectOn(dispatchers.BukkitMain) {
            if (it.isLoading) {
                inventory.clear()
                setManageButtons(clickListener)
                loadingIndicator.display()
            } else {
                loadingIndicator.stop()
                setMenuItems()
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun setMenuItems(model: PlayerRatingsComponent.Model = playerRatingsComponent.model.value) {
        inventory.clear()
        setManageButtons(clickListener)
        sortButton.also(clickListener::remember).setInventoryButton()
        val list = model.userRatings
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= list.size) {
                continue
            }
            val userAndRating = list[index]
            val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
            InventorySlot.Builder {
                this.index = i
                itemStack = PlayerHeadUtil.getHead(userAndRating.userCreatedReport?.normalName ?: "-").apply {
                    editMeta {
                        it.setDisplayName(
                            translation.playerNameColor + (userAndRating.userCreatedReport?.normalName ?: "-")
                        )
                        it.lore = mutableListOf<String>().apply {
                            subListFromString(
                                "${translation.message} $color${userAndRating.message}",
                                config.trimMessageAfter,
                                config.cutWords
                            ).forEachIndexed { _, messagePart ->
                                add("$color$messagePart")
                            }

                            if (config.gui.showFirstConnection) {
                                val firstConnection = translation.firstConnection
                                val time = TimeUtility.formatToString(
                                    time = userAndRating.reportedUser.offlinePlayer.firstPlayed,
                                    format = config.gui.format
                                )
                                add("$firstConnection $time")
                            }
                            if (config.gui.showLastConnection) {
                                val lastConnection = translation.lastConnection
                                val time = TimeUtility.formatToString(
                                    time = userAndRating.reportedUser.offlinePlayer.lastPlayed,
                                    format = config.gui.format
                                )
                                add("$lastConnection $time")
                            }
                            if (AstraPermission.DeleteReport.hasPermission(playerHolder.player) &&
                                config.gui.showDeleteReport
                            ) {
                                add(translation.clickToDeleteReport)
                            }
                        }
                    }
                }
                click = Click { e ->
                    if (!AstraPermission.DeleteReport.hasPermission(playerHolder.player)) return@Click
                    if (e.click != ClickType.LEFT) return@Click
                    val item = model.userRatings.getOrNull(maxItemsPerPage * page + e.slot) ?: return@Click
                    playerRatingsComponent.onDeleteClicked(item)
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }
}
