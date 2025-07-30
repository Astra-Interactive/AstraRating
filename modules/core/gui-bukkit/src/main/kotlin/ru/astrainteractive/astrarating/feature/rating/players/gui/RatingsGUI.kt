package ru.astrainteractive.astrarating.feature.rating.players.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astrarating.core.gui.loading.GuiLoadingIndicator
import ru.astrainteractive.astrarating.core.gui.mapping.UsersRatingsSortMapper
import ru.astrainteractive.astrarating.core.gui.router.GuiRouter
import ru.astrainteractive.astrarating.core.gui.slot.backPageSlot
import ru.astrainteractive.astrarating.core.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.core.gui.slot.nextPageSlot
import ru.astrainteractive.astrarating.core.gui.slot.prevPageSlot
import ru.astrainteractive.astrarating.core.gui.slot.ratingsSlot
import ru.astrainteractive.astrarating.core.gui.slot.ratingsSortSlot
import ru.astrainteractive.astrarating.core.gui.util.normalName
import ru.astrainteractive.astrarating.core.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.rating.players.presentation.RatingPlayersComponent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
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
) : PaginatedInventoryMenu() {

    override val childComponents: List<CoroutineScope>
        get() = listOf(ratingPlayersComponent)

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

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var title: Component = kyoriKrate.getValue()
        .toComponent(translationKrate.getValue().gui.ratingsTitle)

    override val inventorySize: InventorySize = InventorySize.XL

    private val backPageButton
        get() = slotContext.backPageSlot { inventory.close() }

    override val nextPageButton
        get() = slotContext.nextPageSlot

    override val prevPageButton
        get() = slotContext.prevPageSlot

    private val sortButton: InventorySlot
        get() = slotContext.ratingsSortSlot(
            sortType = ratingPlayersComponent.model.value.sort,
            onClick = ratingPlayersComponent::onSortClicked,
            usersRatingsSortMapper = usersRatingsSortMapper
        )

    override var pageContext: PageContext = PageContext(
        maxItemsPerPage = 45,
        page = 0,
        maxItems = ratingPlayersComponent.model.value.userRatings.size
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
        ratingPlayersComponent.model
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

    @Suppress("LongMethod")
    override fun render() {
        val model: RatingPlayersComponent.Model = ratingPlayersComponent.model.value
        inventory.clear()
        setManageButtons()
        sortButton.setInventorySlot()

        for (i in 0 until pageContext.maxItemsPerPage) {
            val index = pageContext.getIndex(i)
            val userAndRating = model.userRatings.getOrNull(index) ?: continue
            slotContext.ratingsSlot(
                index = i,
                firstPlayed = userAndRating.userDTO.offlinePlayer.firstPlayed,
                lastPlayed = userAndRating.userDTO.offlinePlayer.lastPlayed,
                ratingTotal = userAndRating.ratingTotal,
                ratingCounts = userAndRating.ratingCounts,
                playerName = userAndRating.userDTO.normalName,
                click = Click {
                    val route = GuiRouter.Route.PlayerRating(
                        executor = playerHolder.player,
                        selectedPlayerName = userAndRating.userDTO.minecraftName,
                        selectedPlayerUUID = userAndRating.userDTO.minecraftUUID.let(UUID::fromString)
                    )
                    router.navigate(route)
                }
            ).setInventorySlot()
        }
    }
}
