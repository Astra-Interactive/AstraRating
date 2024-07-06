package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiDependencies
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class GuiRouterImpl(
    coreModule: CoreModule,
    apiRatingModule: ApiRatingModule,
    translationContext: KyoriComponentSerializer,
    private val sharedModule: SharedModule
) : GuiRouter {
    private val scope by coreModule.scope
    private val dispatchers = coreModule.dispatchers
    private val guiDependencies by Provider {
        GuiDependencies.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = translationContext
        )
    }

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = route.executor,
                    module = guiDependencies,
                    allRatingsComponent = sharedModule.allRatingsComponentFactory().create(),
                    router = this@GuiRouterImpl
                )

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = route.executor,
                    module = guiDependencies,
                    playerRatingsComponent = sharedModule.playerRatingsComponentFactory(
                        playerName = route.selectedPlayerName
                    ).create(),
                    router = this@GuiRouterImpl
                )
            }
            withContext(dispatchers.Main) {
                gui.open()
            }
        }
    }
}
