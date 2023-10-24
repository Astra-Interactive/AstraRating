package ru.astrainteractive.astrarating.feature.changerating.data

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi

interface CanVoteTodayRepository {
    suspend fun countPlayerTotalDayRated(name: String): Long
}

internal class CanVoteTodayRepositoryImpl(private val dbApi: RatingDBApi) : CanVoteTodayRepository {
    override suspend fun countPlayerTotalDayRated(name: String): Long {
        return dbApi.countPlayerTotalDayRated(name).getOrNull() ?: 0
    }
}
