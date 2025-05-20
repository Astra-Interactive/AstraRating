package ru.astrainteractive.astrarating.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val sharedModule: SharedModule
) : GuiRouter {
    private val scope = coreModule.scope
    private val dispatchers = coreModule.dispatchers

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.AllRatings -> RatingsGUI(
                    player = route.executor,
                    allRatingsComponent = sharedModule.createAllRatingsComponent(),
                    router = this@GuiRouterImpl,
                    dispatchers = coreModule.dispatchers,
                    translation = coreModule.translation.cachedValue,
                    config = coreModule.config.cachedValue,
                    translationContext = kyoriKrate.cachedValue,
                )

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = route.executor,
                    playerRatingsComponent = sharedModule.createPlayerRatingsComponent(
                        playerName = route.selectedPlayerName,
                        playerUUID = route.selectedPlayerUUID
                    ),
                    dispatchers = coreModule.dispatchers,
                    translation = coreModule.translation.cachedValue,
                    config = coreModule.config.cachedValue,
                    translationContext = kyoriKrate.cachedValue,
                    router = this@GuiRouterImpl,
                )
            }
            withContext(dispatchers.Main) {
                gui.open()
            }
        }
    }
}
