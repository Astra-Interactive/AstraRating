package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.di.GuiDependencies
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.astrarating.model.PlayerModel
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
                    selectedPlayer = route.selectedPlayer,
                    player = route.executor,
                    module = guiDependencies,
                    playerRatingsComponent = rootModule.sharedModule.playerRatingsComponentFactory(
                        playerModel = PlayerModel(
                            uuid = route.selectedPlayer.uniqueId,
                            name = route.selectedPlayer.name ?: route.selectedPlayer.uniqueId.toString(),
                            permissible = Bukkit.getPlayer(route.selectedPlayer.uniqueId)?.toPermissible()
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
