package ru.astrainteractive.astrarating.data.exposed.model

sealed interface UsersRatingsSort {
    val isAsc: Boolean

    data class Players(override val isAsc: Boolean) : UsersRatingsSort
}