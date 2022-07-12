package com.astrainteractive.astrarating.api.use_cases

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.sqldatabase.DatabaseCore.Companion.sqlString
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import com.astrainteractive.astrarating.utils.uuid
import github.scarsz.discordsrv.DiscordSRV
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
object InsertUserUseCase : UseCase<Long?, OfflinePlayer>() {
        private val discordUsers = mutableMapOf<String, String>()

    override suspend fun run(params: OfflinePlayer): Long? {
        val discordID = discordUsers[params.uuid] ?: getLinkedDiscordID(params)?.let {
            discordUsers[params.uuid] = it
            it
        }
        return SQLDatabase.selectEntryByID(User.TABLE, User.minecraftUUID.name, params.uuid.sqlString) {
            User.fromResultSet(it)
        }?.id?.let {
            SQLDatabase.updateByID(
                User.TABLE, User.id.name, it,
                User.lastUpdated to System.currentTimeMillis(),
                User.discordID to (discordID?.sqlString ?: "".sqlString)
            )
            it
        } ?: DatabaseApi.insertUserTable(User(params, discordID))?.toLong()

    }
}