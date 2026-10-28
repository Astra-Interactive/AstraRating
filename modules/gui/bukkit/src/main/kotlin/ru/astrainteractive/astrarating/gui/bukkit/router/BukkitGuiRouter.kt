@file:Suppress("FieldDependencyConstruction")

package ru.astrainteractive.astrarating.gui.bukkit.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.gui.api.rating.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.api.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.gui.api.router.api.GuiRouter
import ru.astrainteractive.astrarating.gui.bukkit.rating.player.gui.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.bukkit.rating.players.gui.RatingsGUI
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class BukkitGuiRouter(
    private val coreModule: CoreModule,
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
                    router = this@BukkitGuiRouter,
                    configKrate = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    dispatchers = coreModule.dispatchers
                ).inventory

                is GuiRouter.Route.PlayerRating -> PlayerRatingsGUI(
                    selectedPlayerName = route.selectedPlayerName,
                    player = player,
                    ratingPlayerComponent = ratingPlayerModule.createPlayerRatingsComponent(
                        playerName = route.selectedPlayerName,
                        playerUUID = route.selectedPlayerUUID
                    ),
                    router = this@BukkitGuiRouter,
                    configKrate = coreModule.configKrate,
                    translationKrate = coreModule.translationKrate,
                    dispatchers = coreModule.dispatchers
                ).inventory
            }
            withContext(dispatchers.Main) {
                player.openInventory(inventory)
            }
        }
    }
}
