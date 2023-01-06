package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserAndRating
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO

const val NON_EXISTS_KEY = -1L
interface IRatingAPI {
    suspend fun selectUser(playerName: String): UserDTO?
    suspend fun updateUser(user: UserDTO)
    suspend fun insertUser(user: UserDTO): Long?

    suspend fun insertUserRating(it: UserRatingDTO): Long?
    suspend fun deleteUserRating(it: UserRatingDTO)

    suspend fun fetchUserRatings(playerName: String): List<UserAndRating>?
    suspend fun fetchUsersTotalRating(): List<UserAndRating>?
    suspend fun countPlayerTotalDayRated(playerName: String): Int?
    suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String): Int?
}