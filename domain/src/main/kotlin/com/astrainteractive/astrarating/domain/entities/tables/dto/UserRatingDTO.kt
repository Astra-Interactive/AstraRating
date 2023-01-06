package com.astrainteractive.astrarating.domain.entities.tables.dto

import com.astrainteractive.astrarating.domain.api.NON_EXISTS_KEY

data class UserRatingDTO(
    val id: Long = NON_EXISTS_KEY,
    val userCreatedReport: Long,
    val reportedUser: Long,
    val rating: Int,
    val message: String,
    val time: Long = System.currentTimeMillis(),
)