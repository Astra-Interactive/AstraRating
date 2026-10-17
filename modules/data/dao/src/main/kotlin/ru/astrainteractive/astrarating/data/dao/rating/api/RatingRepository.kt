package ru.astrainteractive.astrarating.data.dao.rating.api

import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import java.util.UUID

interface RatingRepository {
    /**
     * Select user
     * @param playerUUID - name of the player
     */
    suspend fun selectUser(playerUUID: UUID): Result<User>

    /**
     * Update user in database
     * @param user - existed user to update
     */
    suspend fun updateUser(user: User): Result<*>

    /**
     * Insert new user model into database
     * @param user - user model
     */
    suspend fun insertUser(user: NewUser): Result<Long>

    /**
     * Insert user rating value into database
     * @param reporter - player which executing command
     * @param reported - player will be reported
     * @param message - message of reportion
     * @param type - type of reportion
     * @param ratingValue - rating value
     */
    suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>

    /**
     * Delete user rating entry
     * @param it - existed entry
     */
    suspend fun deleteUserRating(it: UserRating): Result<*>

    /**
     * Fetch user rating values
     * @param playerUUID - uuid of the player
     */
    suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRating>>

    suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int>

    /**
     * Fetch users overall ratings
     */
    suspend fun fetchUsersTotalRating(): Result<List<RatedUser>>

    /**
     * Count how much player rated during past 24h
     * @param playerUUID uuid of the player
     */
    suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long>

    /**
     * Count how much player [playerUUID] rated on [ratedPlayerUUID] during 24h
     * @param playerUUID - uuid of executor
     * @param ratedPlayerUUID - uuid of rated player
     */
    suspend fun countPlayerOnPlayerDayRated(
        playerUUID: UUID,
        ratedPlayerUUID: UUID
    ): Result<Long>
}
