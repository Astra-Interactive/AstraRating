package ru.astrainteractive.astrarating.feature.changerating.data

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi

interface CanVoteOnPlayerRepository {
    suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long
}

class CanVoteOnPlayerRepositoryImpl(private val dbApi: RatingDBApi) : CanVoteOnPlayerRepository {
    override suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long {
        return dbApi.countPlayerOnPlayerDayRated(
            creatorName,
            ratedName
        ).getOrNull() ?: 0
    }
}
