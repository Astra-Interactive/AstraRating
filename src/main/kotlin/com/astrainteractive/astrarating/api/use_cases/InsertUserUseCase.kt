package com.astrainteractive.astrarating.api.use_cases

import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.sqldatabase.DatabaseCore.Companion.sqlString
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.utils.uuid
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase : UseCase<Long?, OfflinePlayer>() {
    override suspend fun run(params: OfflinePlayer): Long? {
        return SQLDatabase.selectEntryByID(User.TABLE, User.minecraftUUID.name, params.uuid.sqlString) {
            User.fromResultSet(it)
        }?.id ?: DatabaseApi.insertUserTable(User(params))?.toLong()
    }
}