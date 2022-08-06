package com.astrainteractive.astrarating.sqldatabase

import com.astrainteractive.astralibs.database.ColumnInfo
import com.astrainteractive.astralibs.database.Entity
import com.astrainteractive.astralibs.database.PrimaryKey
import com.astrainteractive.astrarating.api.DatabaseApi
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

data class UserAndRating(
    val reportedPlayer: User,
    val userCreatedReport: User,
    val rating: UserRating,
)

const val NON_EXISTS_KEY = -1L

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
    val offlinePlayer: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))
    val normalName: String
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
            ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName

    companion object {
        const val TABLE = "users"
    }
}

@Entity(UserRating.TABLE)
data class UserRating(
    @ColumnInfo("user_rating_id")
    @PrimaryKey
    val id: Long = NON_EXISTS_KEY,
    @ColumnInfo("user_created_report")
    val userCreatedReport: Long,
    @ColumnInfo("reported_user")
    val reportedUser: Long,
    @ColumnInfo("rating")
    val rating: Int,
    @ColumnInfo("message")
    val message: String,
    @ColumnInfo("time")
    val time: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE = "users_ratings"
    }
}
