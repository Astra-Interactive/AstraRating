package ru.astrainteractive.astrarating.gui.ratings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.slot.backPageSlot
import ru.astrainteractive.astrarating.gui.slot.context.SlotContext
import ru.astrainteractive.astrarating.gui.slot.nextPageSlot
import ru.astrainteractive.astrarating.gui.slot.prevPageSlot
import ru.astrainteractive.astrarating.gui.slot.ratingsSlot
import ru.astrainteractive.astrarating.gui.slot.ratingsSortSlot
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

@Suppress("LongParameterList")
internal class RatingsGUI(
    player: Player,
    private val allRatingsComponent: AllRatingsComponent,
    private val router: GuiRouter,
    private val dispatchers: KotlinDispatchers,
    private val translation: PluginTranslation,
    private val config: EmpireConfig,
    private val translationContext: KyoriComponentSerializer,
) : PaginatedInventoryMenu() {

    override val childComponents: List<CoroutineScope>
        get() = listOf(allRatingsComponent)

    private val loadingIndicator = LoadingIndicator(
        menu = this,
        translation = translation,
        translationContext = translationContext
    )

    private val slotContext = SlotContext(
        translation = translation,
        config = config,
        menu = this,
        kyoriComponentSerializer = translationContext
    )

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var title: Component = translationContext.toComponent(translation.ratingsTitle)

    override val inventorySize: InventorySize = InventorySize.XL

    private val backPageButton
        get() = slotContext.backPageSlot { inventory.close() }

    override val nextPageButton
        get() = slotContext.nextPageSlot

    override val prevPageButton
        get() = slotContext.prevPageSlot

    private val sortButton: InventorySlot
        get() = slotContext.ratingsSortSlot(
            sort = allRatingsComponent.model.value.sort,
            onClick = allRatingsComponent::onSortClicked
        )

    override var pageContext: PageContext = PageContext(
        maxItemsPerPage = 45,
        page = 0,
        maxItems = allRatingsComponent.model.value.userRatings.size
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
        allRatingsComponent.model
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

    @Suppress("LongMethod")
    override fun render() {
        val model: AllRatingsComponent.Model = allRatingsComponent.model.value
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
