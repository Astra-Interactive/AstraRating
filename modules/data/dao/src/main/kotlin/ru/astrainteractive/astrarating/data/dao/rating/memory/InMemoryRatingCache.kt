package ru.astrainteractive.astrarating.data.dao.rating.memory

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import java.util.UUID

internal class InMemoryRatingCache(ratings: Map<UUID, Int>) : RatingCache {
    private val cachedRatings = ratings.toMutableMap()

    override fun resolvePlayerRating(uuid: UUID): Int = cachedRatings[uuid] ?: 0

    override suspend fun loadPlayerRating(uuid: UUID) = Unit

    override fun clear() {
        cachedRatings.clear()
    }
}
