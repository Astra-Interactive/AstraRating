package ru.astrainteractive.astrarating.gui.ratings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.editMeta
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.ratings.di.RatingsGUIDependencies
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.slot.NavigationSlots
import ru.astrainteractive.astrarating.gui.slot.SlotContext
import ru.astrainteractive.astrarating.gui.slot.SortSlots
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class RatingsGUI(
    player: Player,
    private val module: RatingsGUIDependencies,
    private val allRatingsComponent: AllRatingsComponent,
    private val router: GuiRouter
) : PaginatedMenu(),
    RatingsGUIDependencies by module {

    override val childComponents: List<CoroutineScope>
        get() = listOf(allRatingsComponent)

    private val loadingIndicator = LoadingIndicator(
        menu = this,
        translation = translation,
        translationContext = translationContext
    )

    private val slotContext = object : SlotContext {
        override val translation: PluginTranslation = module.translation
        override val config: EmpireConfig = module.config
    }

    private val navigationSlots = NavigationSlots(
        slotContext = slotContext,
        menu = this,
        translationContext = translationContext
    )

    private val sortSlots = SortSlots(
        slotContext = slotContext,
        menu = this,
        translationContext = translationContext
    )

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var menuTitle: Component = translationContext.toComponent(translation.ratingsTitle)

    override val menuSize: MenuSize = MenuSize.XL

    override val backPageButton by Provider {
        navigationSlots.backPageSlot { inventory.close() }
    }

    override val nextPageButton by Provider {
        navigationSlots.nextPageSlot
    }

    override val prevPageButton by Provider {
        navigationSlots.prevPageSlot
    }

    private val sortButton: InventorySlot by Provider {
        sortSlots.ratingsSortSlot(
            sort = allRatingsComponent.model.value.sort,
            onClick = allRatingsComponent::onSortClicked
        )
    }

    override var maxItemsPerPage: Int = 45

    override var page: Int = 0

    override val maxItemsAmount: Int
        get() = allRatingsComponent.model.value.userRatings.size

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        super.onInventoryClose(it)
        allRatingsComponent.close()
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        allRatingsComponent.model
            .onEach {
                if (it.isLoading) {
                    inventory.clear()
                    setManageButtons()
                    runBlocking { loadingIndicator.display() }
                } else {
                    runBlocking { loadingIndicator.stop() }
                    setMenuItems()
                }
            }
            .flowOn(dispatchers.Main)
            .launchIn(menuScope)
    }

    @Suppress("LongMethod")
    private fun setMenuItems(model: AllRatingsComponent.Model = allRatingsComponent.model.value) {
        inventory.clear()
        setManageButtons()
        sortButton.setInventorySlot()

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= model.userRatings.size) {
                continue
            }
            val userAndRating = model.userRatings[index]
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(PlayerHeadUtil.getHead(userAndRating.userDTO.normalName))
                .editMeta {
                    val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
                    displayName(
                        translationContext.toComponent(
                            translation.playerNameColor.raw + userAndRating.userDTO.normalName
                        )
                    )
                    buildList {
                        if (config.gui.showFirstConnection) {
                            val component = translationContext.toComponent(
                                "${translation.firstConnection} ${
                                TimeUtility.formatToString(
                                    time = userAndRating.userDTO.offlinePlayer.firstPlayed,
                                    format = config.gui.format
                                )
                                }"
                            )
                            add(component)
                        }
                        if (config.gui.showLastConnection) {
                            val component = translationContext.toComponent(
                                "${translation.lastConnection} ${
                                TimeUtility.formatToString(
                                    time = userAndRating.userDTO.offlinePlayer.lastPlayed,
                                    format = config.gui.format
                                )
                                }"
                            )
                            add(component)
                        }
                        translationContext
                            .toComponent("${translation.rating}: ${color}${userAndRating.rating}")
                            .run(::add)
                    }
                }
                .setOnClickListener {
                    val route = GuiRouter.Route.PlayerRating(
                        executor = playerHolder.player,
                        selectedPlayerName = userAndRating.userDTO.minecraftName
                    )
                    router.navigate(route)
                }
                .build().setInventorySlot()
        }
    }
}
