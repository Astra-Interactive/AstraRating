package ru.astrainteractive.astrarating.feature.changerating.data

import org.bukkit.Bukkit
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.kdi.Provider

class BukkitPlatformBridge(
    private val minTimeOnServer: Provider<Long>
) : PlatformBridge {
    override fun isPlayerExists(playerModel: PlayerModel?): Boolean {
        val player = playerModel?.uuid?.let(Bukkit::getOfflinePlayer)
        return player == null || player.firstPlayed == 0L
    }

    override fun hasEnoughTime(playerModel: PlayerModel): Boolean {
        val player = playerModel.uuid.let(Bukkit::getOfflinePlayer)
        return System.currentTimeMillis() - player.firstPlayed < minTimeOnServer.provide()
    }
}
