package ru.astrainteractive.astrarating.event.presence

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao

internal class PlayerPresenceListener(
    private val ratingCachedDao: RatingCachedDao
) : EventListener {
    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        plugin.server.onlinePlayers.forEach { player -> ratingCachedDao.markOnline(player.uniqueId) }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ratingCachedDao.markOnline(event.player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ratingCachedDao.markOffline(event.player.uniqueId)
    }
}
