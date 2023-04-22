package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.modules.ServiceLocator
import github.scarsz.discordsrv.util.DiscordUtil
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.math.max


fun getLinkedDiscordID(player: OfflinePlayer) =
    getLinkedDiscordID(player.uniqueId)

fun getLinkedDiscordID(uuid:UUID) =
    AstraRating.discordSRV?.accountLinkManager?.getDiscordId(uuid)

suspend fun getDiscordUser(id: String) = DiscordUtil.getUserById(id)
suspend fun getDiscordMember(id: String) = DiscordUtil.getMemberById(id)

fun subListFromString(text:String, threshold:Int): List<String> {
    val res =  if (ServiceLocator.config.value.cutWords) text.chunked(threshold)
    else text.split(" ").chunked(max(1,(text.length)/threshold)).map {
        it.joinToString(" ")
    }
    return  res
}


