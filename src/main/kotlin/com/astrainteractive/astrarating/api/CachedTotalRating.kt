package com.astrainteractive.astrarating.api

import com.astrainteractive.astralibs.async.AsyncHelper
import kotlinx.coroutines.launch
import org.bukkit.OfflinePlayer
import java.util.*

object CachedTotalRating {
    private val _ratingByPlayer: MutableMap<UUID, Int> = mutableMapOf()
    val ratingByPlayer: Map<UUID, Int>
        get() = _ratingByPlayer

    private suspend fun rememberPlayer(player: OfflinePlayer) {
        DatabaseApi.fetchUserRatings(player)?.sumOf { it.rating.rating }?.let {
            _ratingByPlayer[player.uniqueId] = it
        }
    }

    fun getPlayerRating(player: OfflinePlayer): Int {
        AsyncHelper.launch { rememberPlayer(player) }
        return ratingByPlayer[player.uniqueId] ?: 0
    }
}