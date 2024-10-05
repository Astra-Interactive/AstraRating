package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal fun interface PlayerTotalRatingRepository {
    suspend fun countPlayerTotalDayRated(uuid: UUID): Long
}

internal class PlayerTotalRatingRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : PlayerTotalRatingRepository {
    override suspend fun countPlayerTotalDayRated(uuid: UUID): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerTotalDayRated(uuid).getOrNull() ?: 0
        }
    }
}
