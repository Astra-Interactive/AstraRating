package com.astrainteractive.astrarating.domain.entities.tables.dto

import com.astrainteractive.astrarating.domain.api.NON_EXISTS_KEY

data class UserRatingDTO(
    val id: Int = NON_EXISTS_KEY,
    val userCreatedReport: Int?,
    val reportedUser: Int,
    val rating: Int,
    val message: String,
    val ratingType: RatingTypeDTO,
    val time: Long = System.currentTimeMillis(),
)