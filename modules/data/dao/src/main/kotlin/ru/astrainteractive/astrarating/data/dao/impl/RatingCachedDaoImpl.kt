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
    private val jcache = Cache4kCache<PlayerData, RatingData>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { playerData ->
            val rating = databaseApi.fetchUserRatings(playerData.uuid)
                .getOrNull()
                ?.sumOf { userRatingDTO -> userRatingDTO.rating }
                ?: 0
            RatingData(rating)
        }
    )

    @JvmInline
    private value class RatingData(val rating: Int)
    private data class PlayerData(val name: String, val uuid: UUID)

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        return jcache.getIfPresent(
            PlayerData(
                name,
                uuid
            )
        )?.rating ?: 0
    }

    override fun clear() {
        jcache.invalidateAll()
    }
}
