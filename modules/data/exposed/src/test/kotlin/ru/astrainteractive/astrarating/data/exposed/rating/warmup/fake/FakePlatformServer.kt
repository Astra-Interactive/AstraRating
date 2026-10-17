package ru.astrainteractive.astrarating.data.exposed.rating.warmup.fake

import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

internal class FakePlatformServer(
    private val onlinePlayers: List<OnlineKPlayer>
) : PlatformServer {
    private fun notUsed(): Nothing = error("The tests do not call this")

    override fun getOnlinePlayers(): List<OnlineKPlayer> = onlinePlayers

    override fun findOnlinePlayer(uuid: UUID): OnlineKPlayer = notUsed()

    override fun findOfflinePlayer(uuid: UUID): KPlayer = notUsed()

    override fun findOnlinePlayer(name: String): OnlineKPlayer = notUsed()

    override fun findOfflinePlayer(name: String): KPlayer = notUsed()
}
