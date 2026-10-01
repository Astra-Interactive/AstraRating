package ru.astrainteractive.astrarating.data.dao.impl

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.core.cache.Cache4kCache
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.data.dao.RatingDao
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class RatingCachedDaoImpl(
    private val databaseApi: RatingDao,
    scope: CoroutineScope
) : RatingCachedDao {
    private val ratings = Cache4kCache<UUID, Int>(
        maximumSize = 1_000L,
        updateAfterAccess = 10.seconds,
        coroutineScope = scope,
        update = { uuid -> databaseApi.fetchUserTotalRating(uuid).getOrNull() }
    )

    override fun getPlayerRating(uuid: UUID): Int {
        return ratings.getIfPresent(uuid) ?: 0
    }

    override suspend fun loadPlayerRating(uuid: UUID) {
        ratings.refresh(uuid).join()
    }

    override fun clear() {
        ratings.invalidateAll()
    }
}
