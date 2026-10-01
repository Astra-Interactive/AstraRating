package ru.astrainteractive.astrarating.event.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao

internal class RatingCacheWarmUp(
    private val plugin: Plugin,
    private val ratingCachedDao: RatingCachedDao,
    private val ioScope: CoroutineScope
) : Lifecycle {
    override fun onEnable() {
        plugin.server.onlinePlayers.forEach { player ->
            ioScope.launch { ratingCachedDao.loadPlayerRating(player.uniqueId) }
        }
    }
}
