package com.astrainteractive.astrarating.domain.entities.tables.dto

import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO


data class UserAndRating(
    val reportedPlayer: UserDTO,
    val userCreatedReport: UserDTO,
    val rating: UserRatingDTO,
)