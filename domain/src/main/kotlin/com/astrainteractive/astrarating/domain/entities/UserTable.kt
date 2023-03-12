package com.astrainteractive.astrarating.domain.entities

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table


object UserTable : Table<Int>("users") {
    override val id: Column<Int> = integer("user_id").primaryKey().autoIncrement()
    val minecraftUUID = varchar("minecraft_uuid",64).unique()
    val minecraftName = varchar("minecraft_name",64).unique()
    val discordID = varchar("discord_id",64).unique().nullable()
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