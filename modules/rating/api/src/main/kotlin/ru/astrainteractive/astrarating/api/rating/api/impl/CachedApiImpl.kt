package ru.astrainteractive.astrarating.api.rating.api.impl

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.core.cache.JCache
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class CachedApiImpl(
    private val databaseApi: RatingDBApi,
    scope: CoroutineScope
) : CachedApi {
    private val jcache = JCache<PlayerData, RatingData>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { playerData ->
            val rating = databaseApi.fetchUserRatings(playerData.uuid).getOrNull()?.sumOf {
                it.rating
            } ?: 0
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
        jcache.clear()
    }
}
