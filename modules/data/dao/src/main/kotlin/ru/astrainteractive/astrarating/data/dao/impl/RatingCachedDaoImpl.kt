package ru.astrainteractive.astrarating.data.dao.impl

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.core.cache.Cache4kCache
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.data.dao.RatingDao
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

internal class RatingCachedDaoImpl(
    private val databaseApi: RatingDao,
    scope: CoroutineScope
) : RatingCachedDao {
    private val keptPlayers: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    /**
     * Ratings of online players. A placeholder read is synchronous and shows 0 on a cache miss,
     * so these are dropped on [release] and never by idle time.
     */
    private val keptRatings = Cache4kCache<UUID, RatingData>(
        updateAfterAccess = 10.seconds,
        coroutineScope = scope,
        // A load that finishes after the player left must not put them back
        update = { uuid -> fetchRating(uuid).takeIf { uuid in keptPlayers } }
    )

    private val jcache = Cache4kCache<PlayerData, RatingData>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { playerData -> fetchRating(playerData.uuid) }
    )

    @JvmInline
    private value class RatingData(val rating: Int)
    private data class PlayerData(val name: String, val uuid: UUID)

    private suspend fun fetchRating(uuid: UUID): RatingData {
        val rating = databaseApi.fetchUserRatings(uuid)
            .getOrNull()
            ?.sumOf { userRatingDTO -> userRatingDTO.rating }
            ?: 0
        return RatingData(rating)
    }

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        if (uuid in keptPlayers) {
            return keptRatings.getIfPresent(uuid)?.rating ?: 0
        }
        return jcache.getIfPresent(
            PlayerData(
                name,
                uuid
            )
        )?.rating ?: 0
    }

    override fun keep(uuid: UUID) {
        keptPlayers.add(uuid)
        // Starts the load, so the rating is there before the first placeholder request
        keptRatings.getIfPresent(uuid)
    }

    override fun release(uuid: UUID) {
        keptPlayers.remove(uuid)
        keptRatings.invalidate(uuid)
    }

    override fun clear() {
        keptRatings.invalidateAll()
        jcache.invalidateAll()
    }
}
