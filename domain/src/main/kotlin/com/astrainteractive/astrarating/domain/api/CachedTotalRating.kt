package com.astrainteractive.astrarating.domain.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.PluginScope
import java.util.*

class CachedTotalRating(private val databaseApi: IRatingAPI) {
    private val _ratingByPlayer: MutableMap<UUID, PlayerData> = mutableMapOf()
    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)
    class PlayerData(
        val lastRequestMillis: Long = System.currentTimeMillis(),
        val rating: Int
    )

    private suspend fun rememberPlayer(name: String, uuid: UUID) {
        databaseApi.fetchUserRatings(name ?: "NULL")?.sumOf { it.rating.rating }?.let {
            _ratingByPlayer[uuid] = PlayerData(rating = it)
        }
    }

    fun getPlayerRating(name: String, uuid: UUID): Int? {
        val data = _ratingByPlayer[uuid]
        if (System.currentTimeMillis() - (data?.lastRequestMillis ?: 0L) > 10_000L)
            PluginScope.launch(limitedDispatcher) { rememberPlayer(name, uuid) }
        return data?.rating
    }
}