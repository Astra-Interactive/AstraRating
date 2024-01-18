package ru.astrainteractive.astrarating.gui.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object PlayerHeadUtil {
    fun getHead(player: OfflinePlayer): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta: SkullMeta = item.itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
        item.itemMeta = meta
        return item
    }
    fun getHead(playerName: String) = getHead(Bukkit.getOfflinePlayer(playerName))
}
