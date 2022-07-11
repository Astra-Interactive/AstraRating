package com.astrainteractive.astrarating.api.use_cases

import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.utils.uuid
import com.astrainteractive.astratemplate.api.use_cases.UseCase
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase : UseCase<Long?, OfflinePlayer>() {
    override suspend fun run(params: OfflinePlayer): Long? {
        return DatabaseApi.selectEntryByID(User.TABLE, User.minecraftUUID.name, DatabaseApi.sqlString(params.uuid)) {
            User.fromResultSet(it)
        }?.id ?: DatabaseApi.insertUserTable(User(params))?.toLong()
    }
}