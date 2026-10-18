package ru.astrainteractive.astrarating.data.dao.rating.model

data class RatedUser(
    val user: User,
    val ratingTotal: Int,
    val ratingCounts: Long
)
