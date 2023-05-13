package com.astrainteractive.astrarating.domain.api.impl

import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.util.cache.JCache
import kotlinx.coroutines.CoroutineScope
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class CachedApiImpl(
    private val databaseApi: RatingDBApi,
    scope: CoroutineScope
) : CachedApi {
    private val jcache = JCache<PlayerData, RatingData>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { playerData ->
            val rating = databaseApi.fetchUserRatings(playerData.name).getOrNull()?.sumOf {
                it.rating.rating
            } ?: 0
            RatingData(rating)
        }
    )

    @JvmInline
    private value class RatingData(val rating: Int)
    private data class PlayerData(val name: String, val uuid: UUID)

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        return jcache.getIfPresent(PlayerData(name, uuid))?.rating ?: 0
    }
}
