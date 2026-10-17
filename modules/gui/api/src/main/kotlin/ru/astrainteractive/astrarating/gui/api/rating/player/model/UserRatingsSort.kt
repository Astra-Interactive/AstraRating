package ru.astrainteractive.astrarating.gui.api.rating.player.model

sealed interface UserRatingsSort {
    val isAsc: Boolean

    data class Player(override val isAsc: Boolean) : UserRatingsSort
    data class Date(override val isAsc: Boolean) : UserRatingsSort
    data class Rating(override val isAsc: Boolean) : UserRatingsSort
}
