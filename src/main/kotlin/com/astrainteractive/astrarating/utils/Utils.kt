package com.astrainteractive.astrarating.utils

import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.AstraRating
import com.google.gson.JsonParser
import github.scarsz.discordsrv.util.DiscordUtil
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
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

fun ItemStack.editMeta(metaBuilder: (ItemMeta) -> Unit) {
    val meta = itemMeta ?: return
    metaBuilder(meta)
    this.itemMeta = meta
}

fun Inventory.close() {
    this.viewers?.toList()?.forEach {
        it?.closeInventory()
    }
}

val OfflinePlayer.uuid: String
    get() = uniqueId.toString()
val randomColor: ChatColor
    get() = ChatColor.values()[Random.nextInt(ChatColor.values().size)]

fun getLinkedDiscordID(player: OfflinePlayer) =
    AstraRating.discordSRV?.accountLinkManager?.getDiscordId(player.uniqueId)

suspend fun getDiscordUser(id: String) = DiscordUtil.getUserById(id)
suspend fun getDiscordMember(id: String) = DiscordUtil.getMemberById(id)
fun <T, K> setDeclaredField(clazz: Class<T>, instance: Any, name: String, value: K?) = catching(true) {
    clazz.getDeclaredField(name).run {
        isAccessible = true
        set(instance, value)
        isAccessible = false
    }

}
fun subListFromString(text:String, threshold:Int): List<String> {
    return if (Config.cutWords) text.chunked(threshold)
    else text.split(" ").chunked(max(1,(text.length)/threshold)).map {
        it.joinToString(" ")
    }
}

object TimeUtility {
    fun formatToString(time: Long, format: String = Config.gui.timeFormat): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}