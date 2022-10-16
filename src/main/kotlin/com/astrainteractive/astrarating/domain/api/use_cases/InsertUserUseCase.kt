package com.astrainteractive.astrarating.domain.api.use_cases

import com.astrainteractive.astrarating.domain.api.DatabaseApi
import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import com.astrainteractive.astrarating.utils.uuid
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.domain.IUseCase
import java.sql.Connection
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase(private val databaseApi: DatabaseApi) : IUseCase<Long?, OfflinePlayer> {
    private val discordUsers = mutableMapOf<String, String>()


    override suspend fun run(params: OfflinePlayer): Long? {
        val discordID = discordUsers[params.uuid] ?: getLinkedDiscordID(params)?.let {
            discordUsers[params.uuid] = it
            it
        }
        val user = databaseApi.selectUser(params)
        return user?.let {
            databaseApi.updateUser(it.copy(discordID = discordID))
            it.id
        } ?: databaseApi.insertUser(
            User(
                minecraftName = params.name ?: "UNDEFINED_NAME",
                minecraftUUID = params.uuid,
                discordID = discordID,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
}