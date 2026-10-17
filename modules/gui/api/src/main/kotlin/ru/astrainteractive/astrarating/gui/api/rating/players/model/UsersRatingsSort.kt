package ru.astrainteractive.astrarating.gui.api.rating.players.model

sealed interface UsersRatingsSort {
    val isAsc: Boolean

    data class TotalRating(override val isAsc: Boolean) : UsersRatingsSort
}
