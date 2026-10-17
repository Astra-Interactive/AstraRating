package ru.astrainteractive.astrarating.feature.rating.players.model

sealed interface UsersRatingsSort {
    val isAsc: Boolean

    data class TotalRating(override val isAsc: Boolean) : UsersRatingsSort
}
