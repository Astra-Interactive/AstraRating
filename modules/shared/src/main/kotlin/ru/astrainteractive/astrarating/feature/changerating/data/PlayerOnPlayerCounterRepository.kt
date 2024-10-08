package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal fun interface PlayerOnPlayerCounterRepository {
    suspend fun countPlayerOnPlayerDayRated(creatorUUID: UUID, ratedUUID: UUID): Long
}

internal class PlayerOnPlayerCounterRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : PlayerOnPlayerCounterRepository {
    override suspend fun countPlayerOnPlayerDayRated(creatorUUID: UUID, ratedUUID: UUID): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerOnPlayerDayRated(
                creatorUUID,
                ratedUUID
            ).getOrNull() ?: 0
        }
    }
}
