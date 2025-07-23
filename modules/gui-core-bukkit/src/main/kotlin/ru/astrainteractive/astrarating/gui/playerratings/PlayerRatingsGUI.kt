package ru.astrainteractive.astrarating.gui.playerratings

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
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.ratings.player.presentation.RatingPlayerComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.slot.backPageSlot
import ru.astrainteractive.astrarating.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.gui.slot.killEventSlot
import ru.astrainteractive.astrarating.gui.slot.nextPageSlot
import ru.astrainteractive.astrarating.gui.slot.playerRatingsSlot
import ru.astrainteractive.astrarating.gui.slot.playerRatingsSortSlot
import ru.astrainteractive.astrarating.gui.slot.prevPageSlot
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
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
    private val dispatchers: KotlinDispatchers
) : PaginatedInventoryMenu() {
    override val childComponents: List<CoroutineScope>
        get() = listOf(ratingPlayerComponent)

    private val translation by translationKrate

    private val loadingIndicator = LoadingIndicator(
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
        .toComponent(translation.playerRatingTitle.replace("%player%", selectedPlayerName))

    override val inventorySize: InventorySize = InventorySize.XL

    private val backPageButton: InventorySlot
        get() = slotContext.backPageSlot {
            val route = GuiRouter.Route.AllRatings(player)
            router.navigate(route)
        }

    override val nextPageButton: InventorySlot
        get() = slotContext.nextPageSlot

    override val prevPageButton: InventorySlot
        get() = slotContext.prevPageSlot

    private val sortButton: InventorySlot
        get() = slotContext.playerRatingsSortSlot(
            sort = ratingPlayerComponent.model.value.sort,
            click = { ratingPlayerComponent.onSortClicked() }
        )

    private val killEventSlot: InventorySlot?
        get() = slotContext.killEventSlot(ratingPlayerComponent.model.value.killCounts)

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = 45,
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
                    loadingIndicator.display(menuScope)
                } else {
                    loadingIndicator.stop()
                    render()
                }
            }
            .flowOn(dispatchers.Main)
            .launchIn(menuScope)
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    override fun render() {
        val model: RatingPlayerComponent.Model = ratingPlayerComponent.model.value
        inventory.clear()
        setManageButtons()
        sortButton.setInventorySlot()
        killEventSlot?.setInventorySlot()
        val list = model.userRatings
        for (i in 0 until pageContext.maxItemsPerPage) {
            val index = pageContext.getIndex(i)
            val userAndRating = list.getOrNull(index) ?: continue
            val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
            slotContext.playerRatingsSlot(
                index = i,
                userCreatedReportName = userAndRating.userCreatedReport?.normalName ?: "-",
                color = color,
                message = userAndRating.message,
                firstPlayed = userAndRating.reportedUser.offlinePlayer.firstPlayed,
                lastPlayed = userAndRating.reportedUser.offlinePlayer.lastPlayed,
                canDelete = playerHolder.player
                    .toPermissible()
                    .hasPermission(AstraRatingPermission.DeleteReport),
                click = Click { e ->
                    val canDelete = playerHolder.player
                        .toPermissible()
                        .hasPermission(AstraRatingPermission.DeleteReport)
                    if (!canDelete) return@Click
                    if (e.click != ClickType.LEFT) return@Click
                    val item = model.userRatings
                        .getOrNull(pageContext.maxItemsPerPage * pageContext.page + e.slot)
                        ?: return@Click
                    ratingPlayerComponent.onDeleteClicked(item)
                }
            ).setInventorySlot()
        }
    }
}
