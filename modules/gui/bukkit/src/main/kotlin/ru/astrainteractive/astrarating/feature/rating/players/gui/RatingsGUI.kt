package ru.astrainteractive.astrarating.feature.rating.players.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.core.clear
import ru.astrainteractive.astralibs.menu.core.setInventorySlot
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.api.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.layout.mapSlotsNotNullIndexed
import ru.astrainteractive.astralibs.menu.paginator.api.DefaultPaginator
import ru.astrainteractive.astralibs.menu.paginator.api.context
import ru.astrainteractive.astralibs.menu.paginator.api.openNextPage
import ru.astrainteractive.astralibs.menu.paginator.api.openPrevPage
import ru.astrainteractive.astralibs.menu.paginator.api.setMaxItems
import ru.astrainteractive.astralibs.menu.paginator.model.indexOfSlot
import ru.astrainteractive.astralibs.menu.paginator.model.isFirstPage
import ru.astrainteractive.astralibs.menu.paginator.model.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.gui.layout.DefaultRatingInventoryLayoutFactory
import ru.astrainteractive.astrarating.feature.gui.layout.RatingSlotKey
import ru.astrainteractive.astrarating.feature.gui.loading.GuiLoadingIndicator
import ru.astrainteractive.astrarating.feature.gui.mapping.UsersRatingsSortMapper
import ru.astrainteractive.astrarating.feature.gui.router.GuiRouter
import ru.astrainteractive.astrarating.feature.gui.slot.backPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.feature.gui.slot.nextPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.playerRatingsSortSlot
import ru.astrainteractive.astrarating.feature.gui.slot.prevPageSlot
import ru.astrainteractive.astrarating.feature.gui.slot.ratingsSlot
import ru.astrainteractive.astrarating.feature.gui.util.normalName
import ru.astrainteractive.astrarating.feature.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.feature.rating.players.presentation.RatingPlayersComponent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

@Suppress("LongParameterList")
internal class RatingsGUI(
    player: Player,
    private val dispatchers: KotlinDispatchers,
    private val translationKrate: CachedKrate<AstraRatingTranslation>,
    private val configKratre: CachedKrate<AstraRatingConfig>,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val ratingPlayersComponent: RatingPlayersComponent,
    private val router: GuiRouter,
    private val usersRatingsSortMapper: UsersRatingsSortMapper
) : InventoryMenu() {

    override val childComponents: List<CoroutineScope>
        get() = listOf(ratingPlayersComponent)

    override val menuScope = CoroutineFeature
        .Default(dispatchers.Main)
        .withTimings()

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

    private val playerHolder = DefaultPlayerHolder(player)

    override var title: Component = kyoriKrate.getValue()
        .toComponent(translationKrate.getValue().gui.ratingsTitle)

    override val inventorySize: InventorySize = InventorySize.XL

    private val paginator = DefaultPaginator(
        maxItemsPerPage = inventoryMap.count(RatingSlotKey.RATING_ITEM)
    )

    private val backPageButton: InventorySlot
        get() = slotContext.backPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.BACK),
            click = { playerHolder.player.closeInventory() }
        )

    private val nextPageButton: InventorySlot
        get() = slotContext.nextPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.NEXT_PAGE),
            click = { paginator.openNextPage() }
        )

    private val prevPageButton: InventorySlot
        get() = slotContext.prevPageSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.PREV_PAGE),
            click = { paginator.openPrevPage() }
        )

    private val sortButton: InventorySlot
        get() = slotContext.playerRatingsSortSlot(
            index = inventoryMap.firstIndexOf(RatingSlotKey.SORT),
            sortType = ratingPlayersComponent.model.value.sort,
            click = { ratingPlayersComponent.onSortClicked() },
            usersRatingsSortMapper = usersRatingsSortMapper
        )

    override fun onInventoryClickEvent(e: InventoryClickEvent) {
        super.onInventoryClickEvent(e)
        e.isCancelled = true
    }

    private fun setManageButtons() {
        if (!paginator.context.isFirstPage) setInventorySlot(prevPageButton)
        if (!paginator.context.isLastPage) setInventorySlot(nextPageButton)
        setInventorySlot(backPageButton)
    }

    override fun onInventoryOpenEvent(e: InventoryOpenEvent) {
        ratingPlayersComponent.model
            .onEach {
                paginator.setMaxItems(it.userRatings.size)
                if (it.isLoading) {
                    clear()
                    setManageButtons()
                    guiLoadingIndicator.display(menuScope)
                } else {
                    guiLoadingIndicator.stop()
                    render()
                }
            }
            .flowOn(dispatchers.Main)
            .launchIn(menuScope)

        paginator.paginatorContextStateFlow
            .drop(1)
            .onEach { withContext(dispatchers.Main) { render() } }
            .launchIn(menuScope)
    }

    private val itemSlots: List<InventorySlot>
        get() {
            val model = ratingPlayersComponent.model.value
            return inventoryMap.mapSlotsNotNullIndexed(RatingSlotKey.RATING_ITEM) { itemIndex, slotIndex ->
                val index = paginator.context.indexOfSlot(itemIndex)
                val userAndRating = model.userRatings.getOrNull(index) ?: return@mapSlotsNotNullIndexed null
                slotContext.ratingsSlot(
                    index = slotIndex,
                    firstPlayed = userAndRating.userDTO.offlinePlayer.firstPlayed,
                    lastPlayed = userAndRating.userDTO.offlinePlayer.lastPlayed,
                    ratingTotal = userAndRating.ratingTotal,
                    ratingCounts = userAndRating.ratingCounts,
                    playerName = userAndRating.userDTO.normalName,
                    click = Click {
                        val route = GuiRouter.Route.PlayerRating(
                            inventoryOwner = playerHolder.player.asOnlineMinecraftPlayer(),
                            selectedPlayerName = userAndRating.userDTO.minecraftName,
                            selectedPlayerUUID = userAndRating.userDTO.minecraftUUID.let(UUID::fromString)
                        )
                        router.navigate(route)
                    }
                )
            }
        }

    override fun render() {
        super.render()
        setManageButtons()
        setInventorySlot(sortButton)
        setInventorySlot(itemSlots)
    }
}
