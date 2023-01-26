package com.astrainteractive.astrarating.domain.entities.tables

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table


object UserTable : Table<Int>("users") {
    override val id: Column<Int> = integer("user_id").primaryKey().autoIncrement()
    val minecraftUUID = text("minecraft_uuid").unique()
    val minecraftName = text("minecraft_name").unique()
    val discordID = text("discord_id").unique().nullable()
    val lastUpdated = bigint("last_updated")
}


class UserEntity : Entity<Int>(UserTable) {
    val id by UserTable.id
    val minecraftUUID by UserTable.minecraftUUID
    val minecraftName by UserTable.minecraftName
    var discordID by UserTable.discordID
    var lastUpdated by UserTable.lastUpdated
    companion object: Constructable<UserEntity>(::UserEntity)
}