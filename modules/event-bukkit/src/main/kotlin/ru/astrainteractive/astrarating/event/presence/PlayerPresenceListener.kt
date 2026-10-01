package ru.astrainteractive.astrarating.event.presence

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao

/** Keeps the cached rating of every online player, so the rating placeholder never shows a cold 0 for them. */
internal class PlayerPresenceListener(
    private val ratingCachedDao: RatingCachedDao
) : EventListener {
    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        // Players who are online when the plugin enables never send a join event
        plugin.server.onlinePlayers.forEach { player -> ratingCachedDao.keep(player.uniqueId) }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ratingCachedDao.keep(event.player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ratingCachedDao.release(event.player.uniqueId)
    }
}
