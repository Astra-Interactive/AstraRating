package ru.astrainteractive.astrarating.feature.rating.players.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.core.cache.Cache4kCache
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration.Companion.seconds

internal class RatingPlayersCachedRepositoryImpl(
    private val dbApi: RatingDao,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers
) : RatingPlayersCachedRepository {
    private val jcache = Cache4kCache<Unit, List<RatedUserDTO>>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 1L,
        coroutineScope = coroutineScope,
        update = {
            dbApi.fetchUsersTotalRating().getOrDefault(emptyList())
        }
    )

    override suspend fun fetchUsersTotalRating(): List<RatedUserDTO> {
        return withContext(dispatchers.IO) {
            jcache.get(Unit).orEmpty()
        }
    }

    override fun clear() {
        jcache.invalidateAll()
    }
}
