package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.di.GuiDependencies
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class GuiRouterImpl(
    private val rootModule: RootModule,
) : GuiRouter {
    private val scope by rootModule.servicesModule.scope
    private val dispatchers by rootModule.servicesModule.dispatchers
    private val guiDependencies by Provider {
        GuiDependencies.Default(rootModule)
    }
    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = route.executor,
                    module = guiDependencies,
                    allRatingsComponent = rootModule.sharedModule.allRatingsComponentFactory().create(),
                    router = this@GuiRouterImpl
                )

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = route.executor,
                    module = guiDependencies,
                    playerRatingsComponent = rootModule.sharedModule.playerRatingsComponentFactory(
                        playerName = route.selectedPlayerName
                    ).create(),
                    router = this@GuiRouterImpl
                )
            }
            withContext(dispatchers.BukkitMain) {
                gui.open()
            }
        }
    }
}
