package ru.astrainteractive.astrarating.event.login.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache

internal class RatingCacheWarmUp(
    private val plugin: Plugin,
    private val ratingCache: RatingCache,
    private val ioScope: CoroutineScope
) : Lifecycle {
    override fun onEnable() {
        plugin.server.onlinePlayers.forEach { player ->
            ioScope.launch { ratingCache.loadPlayerRating(player.uniqueId) }
        }
    }
}
