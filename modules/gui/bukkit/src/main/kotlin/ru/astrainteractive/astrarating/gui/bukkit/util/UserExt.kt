package ru.astrainteractive.astrarating.gui.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import java.util.UUID

internal val User.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

internal val User.normalName: String
    get() = offlinePlayer.name
        ?: Bukkit.getOfflinePlayer(minecraftName).name
        ?: minecraftName
