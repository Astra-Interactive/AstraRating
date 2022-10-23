package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserAndRating
import com.astrainteractive.astrarating.domain.entities.UserRating

interface IRatingAPI {
    suspend fun selectUser(playerName: String): User?
    suspend fun updateUser(user: User)
    suspend fun insertUser(user: User): Long?

    suspend fun insertUserRating(it: UserRating): Long?
    suspend fun deleteUserRating(it: UserRating)

    suspend fun fetchUserRatings(playerName: String): List<UserAndRating>?
    suspend fun fetchUsersTotalRating(): List<UserAndRating>?
    suspend fun countPlayerTotalDayRated(playerName: String): Int?
    suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String): Int?
}