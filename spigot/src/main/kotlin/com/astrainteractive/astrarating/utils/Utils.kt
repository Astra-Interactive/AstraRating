package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.modules.ConfigProvider
import github.scarsz.discordsrv.util.DiscordUtil
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.random.Random



fun getLinkedDiscordID(player: OfflinePlayer) =
    getLinkedDiscordID(player.uniqueId)

fun getLinkedDiscordID(uuid:UUID) =
    AstraRating.discordSRV?.accountLinkManager?.getDiscordId(uuid)

suspend fun getDiscordUser(id: String) = DiscordUtil.getUserById(id)
suspend fun getDiscordMember(id: String) = DiscordUtil.getMemberById(id)

fun subListFromString(text:String, threshold:Int): List<String> {
    val res =  if (ConfigProvider.value.cutWords) text.chunked(threshold)
    else text.split(" ").chunked(max(1,(text.length)/threshold)).map {
        it.joinToString(" ")
    }
    return  res
}


object TimeUtility {
    fun formatToString(time: Long, format: String = ConfigProvider.value.gui.format): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}