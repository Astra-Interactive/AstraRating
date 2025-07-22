package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val translationContext: CachedKrate<KyoriComponentSerializer>,
    private val ratingPlayerModule: RatingPlayerModule,
    private val ratingPlayersModule: RatingPlayersModule
) : GuiRouter {
    private val scope = coreModule.scope
    private val dispatchers = coreModule.dispatchers

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = route.executor,
                    ratingPlayersComponent = ratingPlayersModule.createAllRatingsComponent(),
                    router = this@GuiRouterImpl,
                    configKratre = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    kyoriKrate = translationContext,
                    dispatchers = coreModule.dispatchers
                )

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = route.executor,
                    ratingPlayerComponent = ratingPlayerModule.createPlayerRatingsComponent(
                        playerName = route.selectedPlayerName,
                        playerUUID = route.selectedPlayerUUID
                    ),
                    router = this@GuiRouterImpl,
                    configKratre = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    kyoriKrate = translationContext,
                    dispatchers = coreModule.dispatchers
                )
            }
            withContext(dispatchers.Main) {
                gui.open()
            }
        }
    }
}
