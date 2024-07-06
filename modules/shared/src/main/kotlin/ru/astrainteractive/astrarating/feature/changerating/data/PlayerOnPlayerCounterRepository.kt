package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

fun interface PlayerOnPlayerCounterRepository {
    suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long
}

internal class PlayerOnPlayerCounterRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : PlayerOnPlayerCounterRepository {
    override suspend fun countPlayerOnPlayerDayRated(creatorName: String, ratedName: String): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerOnPlayerDayRated(
                creatorName,
                ratedName
            ).getOrNull() ?: 0
        }
    }
}
