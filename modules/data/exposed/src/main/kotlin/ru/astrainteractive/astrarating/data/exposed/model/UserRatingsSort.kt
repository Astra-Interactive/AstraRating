package ru.astrainteractive.astrarating.data.exposed.model

sealed interface UserRatingsSort {
    val isAsc: Boolean

    data class Player(override val isAsc: Boolean) : UserRatingsSort
    data class Date(override val isAsc: Boolean) : UserRatingsSort
    data class Rating(override val isAsc: Boolean) : UserRatingsSort
}
