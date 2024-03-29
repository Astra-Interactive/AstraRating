package ru.astrainteractive.astrarating.dto

data class UserRatingDTO(
    val id: Long,
    val userCreatedReport: UserDTO?,
    val reportedUser: UserDTO,
    val rating: Int,
    val message: String,
    val ratingType: RatingType,
    val time: Long = System.currentTimeMillis(),
)
