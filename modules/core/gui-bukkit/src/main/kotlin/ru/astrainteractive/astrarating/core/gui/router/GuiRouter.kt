package ru.astrainteractive.astrarating.core.gui.router

import org.bukkit.entity.Player
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        class AllRatings(val executor: Player) : Route
        class PlayerRating(
            val executor: Player,
            val selectedPlayerName: String,
            val selectedPlayerUUID: UUID
        ) : Route
    }

    fun navigate(route: Route)
}
