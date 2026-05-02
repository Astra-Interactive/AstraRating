package ru.astrainteractive.astrarating.data.exposed.db.rating.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserTable : LongIdTable("users", "user_id") {
    val minecraftUUID = varchar("minecraft_uuid", MAX_UUID_LENGTH).uniqueIndex()
    val minecraftName = varchar("minecraft_name", MAX_UUID_LENGTH).uniqueIndex()
    val lastUpdated = long("last_updated")
    private const val MAX_UUID_LENGTH = 64
}
