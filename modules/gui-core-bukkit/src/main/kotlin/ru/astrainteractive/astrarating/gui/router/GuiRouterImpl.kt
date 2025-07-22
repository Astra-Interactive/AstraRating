package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.di.GuiDependencies
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val apiRatingModule: ApiRatingModule,
    private val translationContext: KyoriComponentSerializer,
    private val ratingPlayerModule: RatingPlayerModule,
    private val ratingPlayersModule: RatingPlayersModule
) : GuiRouter {
    private val scope = coreModule.scope
    private val dispatchers = coreModule.dispatchers
    private val guiDependencies
        get() = GuiDependencies.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = translationContext
        )

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = route.executor,
                    module = guiDependencies,
                    ratingPlayersComponent = ratingPlayersModule.createAllRatingsComponent(),
                    router = this@GuiRouterImpl
                )

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = route.executor,
                    module = guiDependencies,
                    ratingPlayerComponent = ratingPlayerModule.createPlayerRatingsComponent(
                        playerName = route.selectedPlayerName,
                        playerUUID = route.selectedPlayerUUID
                    ),
                    router = this@GuiRouterImpl
                )
            }
            withContext(dispatchers.Main) {
                gui.open()
            }
        }
    }
}
