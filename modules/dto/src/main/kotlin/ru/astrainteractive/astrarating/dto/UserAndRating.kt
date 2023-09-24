package ru.astrainteractive.astrarating.dto

data class UserAndRating(
    val reportedPlayer: UserDTO,
    val userCreatedReport: UserDTO,
    val rating: UserRatingDTO,
)
