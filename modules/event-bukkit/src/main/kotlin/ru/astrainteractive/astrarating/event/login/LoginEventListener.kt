package ru.astrainteractive.astrarating.event.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import kotlin.time.Duration.Companion.seconds

internal class LoginEventListener(
    private val ratingCachedDao: RatingCachedDao,
    private val scope: CoroutineScope
) : EventListener {
    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        plugin.server.onlinePlayers.forEach { player ->
            scope.launch { ratingCachedDao.loadPlayerRating(player.uniqueId) }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        runBlocking {
            withTimeoutOrNull(RATING_LOAD_TIMEOUT) { ratingCachedDao.loadPlayerRating(event.uniqueId) }
        }
    }

    companion object {
        private val RATING_LOAD_TIMEOUT = 1.seconds
    }
}
