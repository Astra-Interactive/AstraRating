package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.core.cache.util.RefreshingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatedUsersCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import kotlin.time.Duration.Companion.seconds

internal class Cache4kRatedUsersCache(
    private val ratingRepository: RatingRepository,
    coroutineScope: CoroutineScope
) : RatedUsersCache {
    private val cache = RefreshingCache<Unit, List<RatedUser>>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 1L,
        coroutineScope = coroutineScope,
        update = { _ -> ratingRepository.fetchUsersTotalRating().getOrNull() }
    )

    override suspend fun fetchUsersTotalRating(): List<RatedUser> {
        return cache.get(Unit).orEmpty()
    }

    override fun clear() {
        cache.invalidateAll()
    }
}
