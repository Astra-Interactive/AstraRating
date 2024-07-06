package ru.astrainteractive.astrarating.gui.router

import org.bukkit.entity.Player

interface GuiRouter {
    sealed interface Route {
        class AllRatings(val executor: Player) : Route
        class PlayerRating(
            val executor: Player,
            val selectedPlayerName: String
        ) : Route
    }

    fun navigate(route: Route)
}
