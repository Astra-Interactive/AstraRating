package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.core.cache.util.Cache4kCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class Cache4kRatingCache(
    private val databaseApi: RatingRepository,
    scope: CoroutineScope
) : RatingCache {
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
