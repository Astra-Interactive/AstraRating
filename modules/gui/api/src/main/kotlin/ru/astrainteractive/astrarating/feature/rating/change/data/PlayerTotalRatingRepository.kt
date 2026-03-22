package ru.astrainteractive.astrarating.feature.rating.change.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal fun interface PlayerTotalRatingRepository {
    suspend fun countPlayerTotalDayRated(uuid: UUID): Long
}

internal class PlayerTotalRatingRepositoryImpl(
    private val dbApi: RatingDao,
    private val dispatchers: KotlinDispatchers
) : PlayerTotalRatingRepository {
    override suspend fun countPlayerTotalDayRated(uuid: UUID): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerTotalDayRated(uuid).getOrNull() ?: 0
        }
    }
}
