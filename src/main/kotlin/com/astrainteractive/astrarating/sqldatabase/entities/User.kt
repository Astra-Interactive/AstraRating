package com.astrainteractive.astrarating.sqldatabase.entities


import com.astrainteractive.astralibs.catching
import com.astrainteractive.astrarating.api.DatabaseApi.NON_EXISTS_KEY
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.sql.ResultSet

data class User(
    val id: Long = NON_EXISTS_KEY,
    val minecraftUUID: String,
    val minecraftName: String,
    val discordID: String,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    constructor(player: OfflinePlayer) : this(NON_EXISTS_KEY, player.uniqueId.toString(), player.name ?: "null", "")
    constructor(player: CommandSender) : this(NON_EXISTS_KEY, "CONSOLE", player.name ?: "null", "")

    companion object {
        val TABLE: String
            get() = "users"
        val id: EntityInfo
            get() = EntityInfo("id", "INTEGER", primaryKey = true, autoIncrement = true)
        val minecraftUUID: EntityInfo
            get() = EntityInfo("minecraft_uuid", "varchar(16)", unique = true)
        val name: EntityInfo
            get() = EntityInfo("name", "varchar(16)")
        val discordID: EntityInfo
            get() = EntityInfo("discord_id", "varchar(16)", nullable = true)
        val lastUpdated: EntityInfo
            get() = EntityInfo("last_updated", "INTEGER")
        val entities: List<EntityInfo>
            get() = listOf(id, discordID, minecraftUUID, name, lastUpdated)

        fun fromResultSet(rs: ResultSet?) = catching(true) {
            rs?.let {
                return@catching User(
                    id = it.getLong(id.name),
                    minecraftUUID = it.getString(minecraftUUID.name),
                    minecraftName = it.getString(name.name),
                    discordID = it.getString(discordID.name),
                    lastUpdated = it.getLong(lastUpdated.name)
                )
            }
        }
    }
}
