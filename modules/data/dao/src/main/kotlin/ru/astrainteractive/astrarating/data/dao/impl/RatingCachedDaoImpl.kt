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
    /**
     * Every player's total by uuid, loaded with one query and never expired: a placeholder read is synchronous,
     * so a player missing from a per-player cache would be shown 0 until a background load finished.
     */
    private val jcache = Cache4kCache<Unit, Map<String, Int>>(
        updateAfterAccess = 10.seconds,
        maximumSize = 1L,
        coroutineScope = scope,
        update = {
            databaseApi.fetchUsersTotalRating()
                .getOrDefault(emptyList())
                .associateBy(
                    keySelector = { ratedUser -> ratedUser.userDTO.minecraftUUID },
                    valueTransform = { ratedUser -> ratedUser.ratingTotal }
                )
        }
    )

    init {
        // Starts the first load, so the totals are there before the first placeholder request
        jcache.getIfPresent(Unit)
    }

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        return jcache.getIfPresent(Unit)?.get(uuid.toString()) ?: 0
    }

    override fun clear() {
        jcache.invalidateAll()
    }
}
