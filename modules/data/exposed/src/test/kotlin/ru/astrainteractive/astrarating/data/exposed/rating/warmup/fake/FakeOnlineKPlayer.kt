package ru.astrainteractive.astrarating.data.exposed.rating.warmup.fake

import net.kyori.adventure.text.Component
import ru.astrainteractive.astralibs.server.annotation.InternalPlatformApi
import ru.astrainteractive.astralibs.server.location.KLocation
import ru.astrainteractive.astralibs.server.permission.Permission
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID

@OptIn(InternalPlatformApi::class)
internal class FakeOnlineKPlayer(
    override val uuid: UUID,
    override val name: String
) : OnlineKPlayer {
    override val address: InetSocketAddress = InetSocketAddress.createUnresolved("localhost", 0)

    override val locale: Locale = Locale.ROOT

    private fun notUsed(): Nothing = error("The tests do not call this")

    override fun hasPlayedBefore(): Boolean = true

    override fun sendMessage(component: Component) = notUsed()

    override fun getLocation(): KLocation = notUsed()

    override fun teleport(kLocation: KLocation) = notUsed()

    override fun hasPermission(permission: Permission): Boolean = notUsed()

    override fun maxPermissionSize(permission: Permission): Int = notUsed()

    override fun minPermissionSize(permission: Permission): Int = notUsed()

    override fun permissionSizes(permission: Permission): List<Int> = notUsed()

    override fun dispatchCommand(command: String) = notUsed()
}
