package com.astrainteractive.astrarating.utils

import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random

inline fun <reified T : kotlin.Enum<T>> T.addIndex(offset: Int): T {
    val values = T::class.java.enumConstants
    var res = ordinal + offset
    if (res <= -1) res = values.size - 1
    val index = res % values.size
    return values[index]
}

inline fun <reified T : kotlin.Enum<T>> T.next(): T {
    return addIndex(1)
}

inline fun <reified T : kotlin.Enum<T>> T.prev(): T {
    return addIndex(-1)
}

fun ItemStack.editMeta(metaBuilder: (ItemMeta) -> Unit) = itemMeta?.apply {
    metaBuilder(this)
}

fun Inventory.close() {
    this.viewers.forEach {
        it.closeInventory()
    }
}

val OfflinePlayer.uuid: String
    get() = uniqueId.toString()
val randomColor: ChatColor
    get() = ChatColor.values()[Random.nextInt(ChatColor.values().size)]
