package ru.astrainteractive.astrarating.feature.allrating.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.core.cache.JCache
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration.Companion.seconds

internal class AllRatingsCachedRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers
) : AllRatingsCachedRepository {
    private val jcache = JCache<Unit, List<RatedUserDTO>>(
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
        jcache.clear()
    }
}
