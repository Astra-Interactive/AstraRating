package ru.astrainteractive.astrarating.data.exposed.rating.warmup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache

internal class RatingCacheWarmUp(
    private val platformServer: PlatformServer,
    private val ratingCache: RatingCache,
    private val ioScope: CoroutineScope
) : Lifecycle {
    override fun onEnable() {
        platformServer.getOnlinePlayers().forEach { player ->
            ioScope.launch { ratingCache.loadPlayerRating(player.uuid) }
        }
    }
}
