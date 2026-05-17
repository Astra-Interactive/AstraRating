package ru.astrainteractive.astrarating.feature.gui.router

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        val inventoryOwner: OnlineKPlayer
        class AllRatings(override val inventoryOwner: OnlineKPlayer) : Route
        class PlayerRating(
            override val inventoryOwner: OnlineKPlayer,
            val selectedPlayerName: String,
            val selectedPlayerUUID: UUID
        ) : Route
    }

    fun navigate(route: Route)
}
