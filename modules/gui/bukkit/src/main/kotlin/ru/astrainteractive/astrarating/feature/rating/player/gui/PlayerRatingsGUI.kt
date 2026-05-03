package ru.astrainteractive.astrarating.feature.rating.player.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.indexOfSlot
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.layout.mapSlotsNotNullIndexed
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.server.permission.asKPermissible
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.gui.layout.DefaultRatingInventoryLayoutFactory
import ru.astrainteractive.astrarating.feature.gui.layout.RatingSlotKey
import ru.astrainteractive.astrarating.feature.gui.loading.GuiLoadingIndicator
import ru.astrainteractive.astrarating.feature.gui.mapping.UserRatingsSortMapper
import ru.astrainteractive.astrarating.feature.gui.router.GuiRouter
import ru.astrainteractive.astrarating.feature.gui.slot.backPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.feature.gui.slot.killEventSlot
import ru.astrainteractive.astrarating.feature.gui.slot.nextPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.playerRatingsSlot
import ru.astrainteractive.astrarating.feature.gui.slot.prevPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.ratingsSortSlot
import ru.astrainteractive.astrarating.feature.gui.util.normalName
import ru.astrainteractive.astrarating.feature.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.feature.ratings.player.presentation.RatingPlayerComponent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class PlayerRatingsGUI(
    selectedPlayerName: String,
    private val player: Player,
    private val ratingPlayerComponent: RatingPlayerComponent,
    private val router: GuiRouter,
    private val translationKrate: CachedKrate<AstraRatingTranslation>,
    private val configKratre: CachedKrate<AstraRatingConfig>,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val dispatchers: KotlinDispatchers,
    private val userRatingsSortMapper: UserRatingsSortMapper
) : PaginatedInventoryMenu() {
    override val childComponents: List<CoroutineScope>
        get() = listOf(ratingPlayerComponent)

    private val translation by translationKrate

    private val guiLoadingIndicator = GuiLoadingIndicator(
        menu = this,
        translation = translationKrate.getValue(),
        kyori = kyoriKrate.getValue()
    )

    private val slotContext = SlotContext(
        translationKrate = translationKrate,
        configKrate = configKratre,
        kyoriKrate = kyoriKrate,
        menu = this
    )

    private val inventoryMap by lazy { DefaultRatingInventoryLayoutFactory.create() }

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var title: Component = kyoriKrate.getValue()
        .toComponent(translation.gui.playerRatingTitle.replace("%player%", selectedPlayerName))

    override val inventorySize: InventorySize = InventorySize.XL

    private val backPageButton: InventorySlot
        get() = slotContext.backPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.BACK),
            click = {
                val route = GuiRouter.Route.AllRatings(player.asOnlineMinecraftPlayer())
                router.navigate(route)
            }
        )

    override val nextPageButton: InventorySlot
        get() = slotContext.nextPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.NEXT_PAGE)
        )

    override val prevPageButton: InventorySlot
        get() = slotContext.prevPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.PREV_PAGE)
        )

    private val sortButton: InventorySlot
        get() = slotContext.ratingsSortSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.SORT),
            sortType = ratingPlayerComponent.model.value.sort,
            userRatingsSortMapper = userRatingsSortMapper,
            onClick = { ratingPlayerComponent.onSortClicked() }
        )

    private val killEventSlot: InventorySlot?
        get() = slotContext.killEventSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.EVENT),
            killCounts = ratingPlayerComponent.model.value.killCounts
        )

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = inventoryMap.count(RatingSlotKey.RATING_ITEM),
        maxItems = ratingPlayerComponent.model.value.userRatings.size
    )

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    private fun setManageButtons() {
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        backPageButton.setInventorySlot()
    }

    override fun onInventoryCreated() {
        ratingPlayerComponent.model
            .onEach {
                pageContext = pageContext.copy(maxItems = it.userRatings.size)
                if (it.isLoading) {
                    inventory.clear()
                    setManageButtons()
                    guiLoadingIndicator.display(menuScope)
                } else {
                    guiLoadingIndicator.stop()
                    render()
                }
            }
            .flowOn(dispatchers.Main)
            .launchIn(menuScope)
    }

    private val itemSlots: List<InventorySlot>
        get() {
            val model: RatingPlayerComponent.Model = ratingPlayerComponent.model.value
            val list = model.userRatings
            return inventoryMap.mapSlotsNotNullIndexed(RatingSlotKey.RATING_ITEM) { itemIndex, slotIndex ->
                val index = pageContext.indexOfSlot(itemIndex)
                val userAndRating = list.getOrNull(index) ?: return@mapSlotsNotNullIndexed null
                val color = if (userAndRating.rating > 0) {
                    translation.gui.positiveColor
                } else {
                    translation.gui.negativeColor
                }
                slotContext.playerRatingsSlot(
                    index = slotIndex,
                    userCreatedReportName = userAndRating.userCreatedReport?.normalName ?: "-",
                    color = color,
                    message = userAndRating.message,
                    firstPlayed = userAndRating.reportedUser.offlinePlayer.firstPlayed,
                    lastPlayed = userAndRating.reportedUser.offlinePlayer.lastPlayed,
                    canDelete = playerHolder.player
                        .asKPermissible()
                        .hasPermission(AstraRatingPermission.DeleteReport),
                    click = Click { e ->
                        val canDelete = playerHolder.player
                            .asKPermissible()
                            .hasPermission(AstraRatingPermission.DeleteReport)
                        if (!canDelete) return@Click
                        if (e.click != ClickType.LEFT) return@Click
                        val item = list
                            .getOrNull(pageContext.maxItemsPerPage * pageContext.page + e.slot)
                            ?: return@Click
                        ratingPlayerComponent.onDeleteClicked(item)
                    }
                )
            }
        }

    override fun render() {
        inventory.clear()
        setManageButtons()
        sortButton.setInventorySlot()
        killEventSlot?.setInventorySlot()
        itemSlots.forEach { slot -> slot.setInventorySlot() }
    }
}
