package ru.astrainteractive.astrarating.gui.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.time.Instant

internal val OfflinePlayer.firstPlayedAt: Instant?
    get() = firstPlayed.takeIf { millis -> millis != 0L }?.let(Instant::fromEpochMilliseconds)

internal val OfflinePlayer.lastPlayedAt: Instant?
    get() = lastPlayed.takeIf { millis -> millis != 0L }?.let(Instant::fromEpochMilliseconds)

internal fun OfflinePlayer.toHeadItemStack(): ItemStack {
    val item = ItemStack(Material.PLAYER_HEAD)
    val meta: SkullMeta = item.itemMeta as SkullMeta
    meta.owningPlayer = Bukkit.getOfflinePlayer(uniqueId)
    item.itemMeta = meta
    return item
}
