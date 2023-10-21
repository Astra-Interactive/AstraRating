package ru.astrainteractive.astrarating.api.rating.api

import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserAndRating
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.UserModel

interface RatingDBApi {
    /**
     * Select user
     * @param playerName - name of the player
     */
    suspend fun selectUser(playerName: String): Result<UserDTO>

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
     * @param playerName - name of the player
     */
    suspend fun fetchUserRatings(playerName: String): Result<List<UserAndRating>>

    /**
     * Fetch users overall ratings
     */
    suspend fun fetchUsersTotalRating(): Result<List<UserAndRating>>

    /**
     * Count how much player rated during past 24h
     * @param playerName name of the player
     */
    suspend fun countPlayerTotalDayRated(playerName: String): Result<Long>

    /**
     * Count how much player [playerName] reated on [ratedPlayerName] during 24h
     * @param playerName - name of executor
     * @param ratedPlayerName - name of rated player
     */
    suspend fun countPlayerOnPlayerDayRated(
        playerName: String,
        ratedPlayerName: String
    ): Result<Long>
}
