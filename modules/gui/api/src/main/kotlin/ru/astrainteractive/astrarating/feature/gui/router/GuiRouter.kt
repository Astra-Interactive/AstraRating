package ru.astrainteractive.astrarating.feature.gui.router

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        class AllRatings(val executor: OnlineKPlayer) : Route
        class PlayerRating(
            val executor: OnlineKPlayer,
            val selectedPlayerName: String,
            val selectedPlayerUUID: UUID
        ) : Route
    }

    fun navigate(route: Route)
}
