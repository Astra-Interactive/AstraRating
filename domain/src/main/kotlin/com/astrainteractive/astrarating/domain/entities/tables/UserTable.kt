package com.astrainteractive.astrarating.domain.entities.tables

import ru.astrainteractive.astralibs.database_v2.Column
import ru.astrainteractive.astralibs.database_v2.Entity
import ru.astrainteractive.astralibs.database_v2.Table


object UserTable : Table<Long>("users") {
    override val id: Column<Long> = long("user_id").primaryKey().autoIncrement()
    val minecraftUUID = text("minecraft_uuid").unique()
    val minecraftName = text("minecraft_name").unique()
    val discordID = text("discord_id").unique()
    val lastUpdated = long("last_updated")
}


class UserEntity : Entity<Long>(UserTable) {
    val id by UserTable.id
    val minecraftUUID by UserTable.minecraftUUID
    val minecraftName by UserTable.minecraftName
    var discordID by UserTable.discordID
    var lastUpdated by UserTable.lastUpdated
}