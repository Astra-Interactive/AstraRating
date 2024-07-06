package ru.astrainteractive.astrarating.db.rating.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable("users", "user_id") {
    val minecraftUUID = varchar("minecraft_uuid", 64).uniqueIndex()
    val minecraftName = varchar("minecraft_name", 64).uniqueIndex()
    val discordID = varchar("discord_id", 64).uniqueIndex().nullable()
    val lastUpdated = long("last_updated")
}

class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    val minecraftUUID by UserTable.minecraftUUID
    val minecraftName by UserTable.minecraftName
    var lastUpdated by UserTable.lastUpdated
    val rating by UserRatingDAO referrersOn UserRatingTable.reportedUser

    companion object : LongEntityClass<UserDAO>(UserTable)
}
