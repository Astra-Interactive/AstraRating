package com.astrainteractive.astrarating.api.use_cases

import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.sqldatabase.User
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import com.astrainteractive.astrarating.utils.uuid
import org.bukkit.OfflinePlayer
import java.sql.Connection
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
object InsertUserUseCase : UseCase<Long?, OfflinePlayer>() {
    private val discordUsers = mutableMapOf<String, String>()

    val database: SQLDatabase?
        get() = SQLDatabase.instance
    val connection: Connection?
        get() = database?.connection

    override suspend fun run(params: OfflinePlayer): Long? {
        val discordID = discordUsers[params.uuid] ?: getLinkedDiscordID(params)?.let {
            discordUsers[params.uuid] = it
            it
        }
        val user = DatabaseApi.selectUser(params)
        return user?.let {
            DatabaseApi.updateUser(it.copy(discordID = discordID))
            it.id
        } ?: DatabaseApi.insertUser(
            User(
                minecraftName = params.name ?: "UNDEFINED_NAME",
                minecraftUUID = params.uuid,
                discordID = discordID,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
}