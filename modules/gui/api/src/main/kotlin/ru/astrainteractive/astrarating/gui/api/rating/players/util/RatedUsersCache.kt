package ru.astrainteractive.astrarating.gui.api.rating.players.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.core.cache.util.Cache4kCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration.Companion.seconds

internal class RatedUsersCache(
    private val ratingRepository: RatingRepository,
    private val dispatchers: KotlinDispatchers,
    coroutineScope: CoroutineScope
) {
    private val cache = Cache4kCache<Unit, List<RatedUser>>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 1L,
        coroutineScope = coroutineScope,
        update = { _ -> ratingRepository.fetchUsersTotalRating().getOrNull() }
    )

    suspend fun fetchUsersTotalRating(): List<RatedUser> {
        return withContext(dispatchers.IO) {
            cache.get(Unit).orEmpty()
        }
    }

    fun clear() {
        cache.invalidateAll()
    }
}
