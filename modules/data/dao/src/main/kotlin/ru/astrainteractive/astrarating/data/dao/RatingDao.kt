package ru.astrainteractive.astrarating.data.dao

import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import java.util.UUID

interface RatingDao {
    /**
     * Select user
     * @param playerUUID - name of the player
     */
    suspend fun selectUser(playerUUID: UUID): Result<UserDTO>

    /**
     * Update user in database
     * @param user - existed user to update
     */
    suspend fun updateUser(user: UserDTO): Result<*>

    /**
     * Insert new user model into database
     * @param user - user model
     */
    suspend fun insertUser(user: UserModel): Result<Long>

    /**
     * Insert user rating value into database
     * @param reporter - player which executing command
     * @param reported - player will be reported
     * @param message - message of reportion
     * @param type - type of reportion
     * @param ratingValue - rating value
     */
    suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>

    /**
     * Delete user rating entry
     * @param it - existed entry
     */
    suspend fun deleteUserRating(it: UserRatingDTO): Result<*>

    /**
     * Fetch user rating values
     * @param playerUUID - uuid of the player
     */
    suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRatingDTO>>

    /**
     * Fetch users overall ratings
     */
    suspend fun fetchUsersTotalRating(): Result<List<RatedUserDTO>>

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
