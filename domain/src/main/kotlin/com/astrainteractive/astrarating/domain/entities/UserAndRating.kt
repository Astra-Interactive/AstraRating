package com.astrainteractive.astrarating.domain.entities


data class UserAndRating(
    val reportedPlayer: User,
    val userCreatedReport: User,
    val rating: UserRating,
)