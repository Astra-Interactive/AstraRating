package com.astrainteractive.astrarating.domain.entities

import com.astrainteractive.astrarating.domain.SQLDatabase.Companion.NON_EXISTS_KEY
import ru.astrainteractive.astralibs.database.ColumnInfo
import ru.astrainteractive.astralibs.database.Entity
import ru.astrainteractive.astralibs.database.PrimaryKey


@Entity(User.TABLE)
data class User(
    @ColumnInfo("user_id")
    @PrimaryKey
    val id: Long = NON_EXISTS_KEY,
    @ColumnInfo("minecraft_uuid")
    val minecraftUUID: String = "",
    @ColumnInfo("minecraft_name")
    val minecraftName: String = "",
    @ColumnInfo("discord_id", nullable = true)
    val discordID: String? = "",
    @ColumnInfo("last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE = "users"
    }
}
