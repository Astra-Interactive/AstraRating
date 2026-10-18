package ru.astrainteractive.astrarating.data.dao.rating.api

import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import java.util.UUID

interface RatingRepository {
    suspend fun findUser(playerUUID: UUID): Result<User?>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun insertUser(user: NewUser): Result<User>

    suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>

    suspend fun deleteUserRating(rating: UserRating): Result<Unit>

    suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRating>>

    suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int>

    suspend fun fetchUsersTotalRating(): Result<List<RatedUser>>

    /**
     * Count how much player rated during past 24h
     */
    suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long>

    /**
     * Count how much player [playerUUID] rated on [ratedPlayerUUID] during 24h
     */
    suspend fun countPlayerOnPlayerDayRated(
        playerUUID: UUID,
        ratedPlayerUUID: UUID
    ): Result<Long>
}
