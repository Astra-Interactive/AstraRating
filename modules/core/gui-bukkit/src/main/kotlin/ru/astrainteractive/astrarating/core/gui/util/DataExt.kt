@file:Suppress("Filename")

package ru.astrainteractive.astrarating.core.gui.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import java.util.UUID

internal val UserDTO.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

internal val UserDTO.normalName: String
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
        ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName
