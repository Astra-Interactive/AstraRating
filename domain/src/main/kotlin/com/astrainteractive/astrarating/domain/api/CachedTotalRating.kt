package com.astrainteractive.astrarating.domain.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.PluginScope
import java.util.*

class CachedTotalRating(private val databaseApi: IRatingAPI) {
    private val _ratingByPlayer: MutableMap<UUID, Int> = mutableMapOf()
    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)
    val ratingByPlayer: Map<UUID, Int>
        get() = _ratingByPlayer

    private suspend fun rememberPlayer(name: String, uuid: UUID) {
        databaseApi.fetchUserRatings(name ?: "NULL")?.sumOf { it.rating.rating }?.let {
            _ratingByPlayer[uuid] = it
        }
    }

    fun getPlayerRating(name: String, uuid: UUID): Int {
        PluginScope.launch(limitedDispatcher) { rememberPlayer(name, uuid) }
        return ratingByPlayer[uuid] ?: 0
    }
}