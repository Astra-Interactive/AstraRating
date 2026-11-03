package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class Cache4kRatingCache(
    ratingRepository: RatingRepository,
    scope: CoroutineScope
) : RatingCache {
    private val ratings = RefreshingCache<UUID, Int>(
        maximumSize = 1_000L,
        updateAfterAccess = 10.seconds,
        coroutineScope = scope,
        update = { uuid -> ratingRepository.fetchUserTotalRating(uuid).getOrNull() }
    )

    override fun resolvePlayerRating(uuid: UUID): Int {
        return ratings.findCached(uuid) ?: 0
    }

    override suspend fun loadPlayerRating(uuid: UUID) {
        ratings.refresh(uuid).join()
    }

    override fun clear() {
        ratings.invalidateAll()
    }
}
