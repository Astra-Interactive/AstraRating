package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.di.GuiDependencies
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.astrarating.model.PlayerModel

class GuiRouterImpl(
    private val scope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    private val rootModule: RootModule,
    private val guiDependencies: GuiDependencies
) : GuiRouter {
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
                    selectedPlayer = route.selectedPlayer,
                    player = route.executor,
                    module = guiDependencies,
                    playerRatingsComponent = rootModule.sharedModule.playerRatingsComponentFactory(
                        playerModel = PlayerModel(
                            uuid = route.selectedPlayer.uniqueId,
                            name = route.selectedPlayer.name ?: route.selectedPlayer.uniqueId.toString()
                        )
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
