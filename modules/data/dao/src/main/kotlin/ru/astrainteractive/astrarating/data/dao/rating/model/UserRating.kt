@file:Suppress("BareIdentifier")

package ru.astrainteractive.astrarating.data.dao.rating.model

import kotlin.time.Instant

data class UserRating(
    val id: Long,
    val userCreatedReport: User?,
    val reportedUser: User,
    val rating: Int,
    val message: String,
    val ratingType: RatingType,
    val time: Instant,
)
