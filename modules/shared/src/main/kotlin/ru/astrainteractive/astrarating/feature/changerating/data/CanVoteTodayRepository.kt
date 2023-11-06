package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CanVoteTodayRepository {
    suspend fun countPlayerTotalDayRated(name: String): Long
}

internal class CanVoteTodayRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : CanVoteTodayRepository {
    override suspend fun countPlayerTotalDayRated(name: String): Long {
        return withContext(dispatchers.IO) {
            dbApi.countPlayerTotalDayRated(name).getOrNull() ?: 0
        }
    }
}
