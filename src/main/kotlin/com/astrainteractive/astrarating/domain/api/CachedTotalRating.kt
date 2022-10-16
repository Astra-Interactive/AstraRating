package com.astrainteractive.astrarating.domain.api

import kotlinx.coroutines.launch
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.async.PluginScope
import java.util.*

class CachedTotalRating(private val databaseApi: DatabaseApi) {
    private val _ratingByPlayer: MutableMap<UUID, Int> = mutableMapOf()
    val ratingByPlayer: Map<UUID, Int>
        get() = _ratingByPlayer

    private suspend fun rememberPlayer(player: OfflinePlayer) {
        databaseApi.fetchUserRatings(player)?.sumOf { it.rating.rating }?.let {
            _ratingByPlayer[player.uniqueId] = it
        }
    }

    fun getPlayerRating(player: OfflinePlayer): Int {
        PluginScope.launch { rememberPlayer(player) }
        return ratingByPlayer[player.uniqueId] ?: 0
    }
}