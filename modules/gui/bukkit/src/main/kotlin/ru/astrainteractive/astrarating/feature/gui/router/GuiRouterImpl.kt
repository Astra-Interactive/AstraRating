package ru.astrainteractive.astrarating.feature.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.gui.mapping.UserRatingsSortMapper
import ru.astrainteractive.astrarating.feature.gui.mapping.UsersRatingsSortMapper
import ru.astrainteractive.astrarating.feature.rating.player.gui.PlayerRatingsGUI
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.rating.players.gui.RatingsGUI
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val translationContext: CachedKrate<KyoriComponentSerializer>,
    private val ratingPlayerModule: RatingPlayerModule,
    private val ratingPlayersModule: RatingPlayersModule
) : GuiRouter {
    private val scope = coreModule.ioScope
    private val dispatchers = coreModule.dispatchers

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val player = route.inventoryOwner.tryCast<BukkitOnlineKPlayer>()
                ?.instance
                ?: error("Could not cast player to BukkitOnlineKPlayer")
            val inventory = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = player,
                    ratingPlayersComponent = ratingPlayersModule.createAllRatingsComponent(),
                    router = this@GuiRouterImpl,
                    configKratre = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    kyoriKrate = translationContext,
                    dispatchers = coreModule.dispatchers,
                    usersRatingsSortMapper = UsersRatingsSortMapper(coreModule.translationKrate)
                ).inventory

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = player,
                    ratingPlayerComponent = ratingPlayerModule.createPlayerRatingsComponent(
                        playerName = route.selectedPlayerName,
                        playerUUID = route.selectedPlayerUUID
                    ),
                    router = this@GuiRouterImpl,
                    configKratre = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    kyoriKrate = translationContext,
                    dispatchers = coreModule.dispatchers,
                    userRatingsSortMapper = UserRatingsSortMapper(coreModule.translationKrate)
                ).inventory
            }
            withContext(dispatchers.Main) {
                player.openInventory(inventory)
            }
        }
    }
}
