package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CanVoteOnPlayerRepository {
    suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long
}

internal class CanVoteOnPlayerRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : CanVoteOnPlayerRepository {
    override suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerOnPlayerDayRated(
                creatorName,
                ratedName
            ).getOrNull() ?: 0
        }
    }
}
