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
    private val onlinePlayers: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    private val onlineRatings = Cache4kCache<UUID, RatingData>(
        updateAfterAccess = 10.seconds,
        coroutineScope = scope,
        update = { uuid -> fetchRatingIfStillOnline(uuid) }
    )

    private val offlineRatings = Cache4kCache<PlayerData, RatingData>(
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

    private suspend fun fetchRatingIfStillOnline(uuid: UUID): RatingData? {
        val rating = fetchRating(uuid)
        return rating.takeIf { uuid in onlinePlayers }
    }

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        if (uuid in onlinePlayers) {
            return onlineRatings.getIfPresent(uuid)?.rating ?: 0
        }
        return offlineRatings.getIfPresent(
            PlayerData(
                name,
                uuid
            )
        )?.rating ?: 0
    }

    override fun markOnline(uuid: UUID) {
        onlinePlayers.add(uuid)
        onlineRatings.refresh(uuid)
    }

    override fun markOffline(uuid: UUID) {
        onlinePlayers.remove(uuid)
        onlineRatings.invalidate(uuid)
    }

    override fun clear() {
        onlineRatings.invalidateAll()
        offlineRatings.invalidateAll()
    }
}
