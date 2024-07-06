package ru.astrainteractive.astrarating.gui.playerratings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setMaterial
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.RatingPermission
import ru.astrainteractive.astrarating.feature.playerrating.presentation.PlayerRatingsComponent
import ru.astrainteractive.astrarating.gui.loading.LoadingIndicator
import ru.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiDependencies
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.slot.NavigationSlots
import ru.astrainteractive.astrarating.gui.slot.SlotContext
import ru.astrainteractive.astrarating.gui.slot.SortSlots
import ru.astrainteractive.astrarating.gui.util.PlayerHeadUtil
import ru.astrainteractive.astrarating.gui.util.TimeUtility
import ru.astrainteractive.astrarating.gui.util.normalName
import ru.astrainteractive.astrarating.gui.util.offlinePlayer
import ru.astrainteractive.astrarating.gui.util.subListFromString

internal class PlayerRatingsGUI(
    selectedPlayerName: String,
    private val player: Player,
    private val module: PlayerRatingGuiDependencies,
    private val playerRatingsComponent: PlayerRatingsComponent,
    private val router: GuiRouter
) : PaginatedInventoryMenu(), PlayerRatingGuiDependencies by module {
    override val childComponents: List<CoroutineScope>
        get() = listOf(playerRatingsComponent)

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
        translationContext = translationContext
    )

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override var title: Component = translationContext.toComponent(
        translation.playerRatingTitle.replace("%player%", selectedPlayerName)
    )

    override val inventorySize: InventorySize = InventorySize.XL

    private val backPageButton: InventorySlot
        get() = navigationSlots.backPageSlot {
            val route = GuiRouter.Route.AllRatings(player)
            router.navigate(route)
        }

    override val nextPageButton: InventorySlot
        get() = navigationSlots.nextPageSlot

    override val prevPageButton: InventorySlot
        get() = navigationSlots.prevPageSlot

    private val sortButton: InventorySlot
        get() = sortSlots.playerRatingsSortSlot(
            sort = playerRatingsComponent.model.value.sort,
            click = { playerRatingsComponent.onSortClicked() }
        )

    private val killEventSlot: InventorySlot?
        get() = InventorySlot.Builder()
            .setIndex(46)
            .setMaterial(Material.NETHERITE_SWORD)
            .editMeta {
                translationContext
                    .toComponent(translation.eventsTitle)
                    .run(::displayName)
            }
            .addLore(
                translation.eventKillAmount(
                    playerRatingsComponent.model.value.killCounts
                ).let(translationContext::toComponent)
            )
            .build()
            .takeIf { playerRatingsComponent.model.value.killCounts > 0 }

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = 45,
        maxItems = playerRatingsComponent.model.value.userRatings.size
    )

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClosed(it: InventoryCloseEvent) {
        playerRatingsComponent.close()
        super.onInventoryClosed(it)
    }

    private fun setManageButtons() {
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        backPageButton.setInventorySlot()
    }

    override fun onInventoryCreated() {
        playerRatingsComponent.model
            .onEach {
                pageContext = pageContext.copy(maxItems = it.userRatings.size)
                if (it.isLoading) {
                    inventory.clear()
                    setManageButtons()
                    loadingIndicator.display()
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
        val model: PlayerRatingsComponent.Model = playerRatingsComponent.model.value
        inventory.clear()
        setManageButtons()
        sortButton.setInventorySlot()
        killEventSlot?.setInventorySlot()
        val list = model.userRatings
        for (i in 0 until pageContext.maxItemsPerPage) {
            val index = pageContext.getIndex(i)
            val userAndRating = list.getOrNull(index) ?: continue
            val color = if (userAndRating.rating > 0) translation.positiveColor else translation.negativeColor
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(PlayerHeadUtil.getHead(userAndRating.userCreatedReport?.normalName ?: "-"))
                .editMeta {
                    displayName(
                        translationContext.toComponent(
                            translation.playerNameColor.raw + (
                                    userAndRating.userCreatedReport?.normalName
                                        ?: "-"
                                    )
                        )
                    )
                    buildList<Component> {
                        subListFromString(
                            "${translation.message.raw} ${color.raw}${userAndRating.message}",
                            config.trimMessageAfter,
                            config.cutWords
                        ).forEachIndexed { _, messagePart ->
                            val component = translationContext.toComponent("${color.raw}$messagePart")
                            add(component)
                        }

                        if (config.gui.showFirstConnection) {
                            val firstConnection = translation.firstConnection.raw
                            val time = TimeUtility.formatToString(
                                time = userAndRating.reportedUser.offlinePlayer.firstPlayed,
                                format = config.gui.format
                            )
                            val component = translationContext.toComponent("$firstConnection $time")
                            add(component)
                        }
                        if (config.gui.showLastConnection) {
                            val lastConnection = translation.lastConnection.raw
                            val time = TimeUtility.formatToString(
                                time = userAndRating.reportedUser.offlinePlayer.lastPlayed,
                                format = config.gui.format
                            )
                            val component = translationContext.toComponent("$lastConnection $time")
                            add(component)
                        }
                        val canDelete = playerHolder.player.toPermissible().hasPermission(
                            RatingPermission.DeleteReport
                        )

                        if (canDelete && config.gui.showDeleteReport) {
                            val component = translationContext.toComponent(translation.clickToDeleteReport)
                            add(component)
                        }
                    }.run(::lore)
                }
                .setOnClickListener { e ->
                    val canDelete = playerHolder.player.toPermissible().hasPermission(
                        RatingPermission.DeleteReport
                    )
                    if (!canDelete) return@setOnClickListener
                    if (e.click != ClickType.LEFT) return@setOnClickListener
                    val item = model.userRatings.getOrNull(pageContext.maxItemsPerPage * pageContext.page + e.slot)
                        ?: return@setOnClickListener
                    playerRatingsComponent.onDeleteClicked(item)
                }
                .build()
                .setInventorySlot()
        }
    }
}
