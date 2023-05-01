package com.astrainteractive.astrarating.dto

data class UserRatingDTO(
    val id: Int,
    val userCreatedReport: Int?,
    val reportedUser: Int,
    val rating: Int,
    val message: String,
    val ratingType: RatingType,
    val time: Long = System.currentTimeMillis(),
)
