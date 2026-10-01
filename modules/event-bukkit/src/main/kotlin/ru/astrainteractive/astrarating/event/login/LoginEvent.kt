package ru.astrainteractive.astrarating.event.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao

internal class LoginEvent(
    plugin: Plugin,
    ratingCachedDao: RatingCachedDao,
    mainScope: CoroutineScope,
    ioScope: CoroutineScope
) {
    val playerPreLoginEvent: Job = flowEvent<AsyncPlayerPreLoginEvent>(
        plugin = plugin,
        eventPriority = EventPriority.MONITOR
    )
        .filterIsInstance<AsyncPlayerPreLoginEvent>()
        .filter { event -> event.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED }
        .onEach { event -> ioScope.launch { ratingCachedDao.loadPlayerRating(event.uniqueId) } }
        .launchIn(mainScope)
}
