package com.astrainteractive.astrarating.api

import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.mapNotNull
import com.astrainteractive.astrarating.sqldatabase.DatabaseCore.Companion.sqlString
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.sqldatabase.entities.UserAndRating
import com.astrainteractive.astrarating.sqldatabase.entities.UserRating
import com.astrainteractive.astrarating.utils.uuid
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.sql.Connection


/**
 * API with all SQL commands
 */
object DatabaseApi {
    private val connection: Connection?
        get() = SQLDatabase.connection
    const val NON_EXISTS_KEY = 0L
    private val TAG = "DatabaseApi"


    suspend fun createUsersTable() = catching {
        return@catching SQLDatabase.createTable(User.TABLE, User.entities)
    }

    suspend fun createUsersRatingsTable() = catching {
        return@catching SQLDatabase.createTable(UserRating.TABLE, UserRating.entities)
    }


    suspend fun insertUserTable(it: User): Int? {
        return SQLDatabase.insert(
            User.TABLE,
            User.minecraftUUID to it.minecraftUUID.sqlString,
            User.minecraftName to it.minecraftName.sqlString,
            User.discordID to it.discordID.sqlString,
            User.lastUpdated to it.lastUpdated
        )
    }

    suspend fun insertUserRating(it: UserRating) {
        SQLDatabase.insert(
            UserRating.TABLE,
            UserRating.userCreatedReport to it.userCreatedReport,
            UserRating.reportedUser to it.reportedUser,
            UserRating.rating to it.rating,
            UserRating.message to it.message.sqlString,
            UserRating.time to it.time
        )
    }

    suspend fun deleteUserRating(it: UserRating) {
        SQLDatabase.deleteEntryByID(UserRating.TABLE, UserRating.id.name, it.id)
    }

    suspend fun fetchUserRatings(player: OfflinePlayer): List<UserAndRating>? {
        val query =
            "SELECT * FROM ${UserRating.TABLE} A JOIN ${User.TABLE} B on A.${UserRating.userCreatedReport.name}=B.${User.id.name} WHERE A.${UserRating.reportedUser.name}=(SELECT ${User.id.name} FROM ${User.TABLE} WHERE ${User.minecraftName.name}=${player.name?.sqlString} LIMIT 1)"
        val rs = SQLDatabase.connection?.createStatement()?.executeQuery(query)
        val reportedUser = SQLDatabase.selectEntryByID(User.TABLE, User.minecraftUUID.name, player.uuid.sqlString) {
            User.fromResultSet(it)
        } ?: return null
        return rs?.mapNotNull {
            val userCreatedReport = User.fromResultSet(it) ?: return@mapNotNull null
            val rating = UserRating.fromResultSet(it) ?: return@mapNotNull null
            UserAndRating(reportedUser, userCreatedReport, rating)
        }
    }

    suspend fun fetchUsersTotalRating(): List<UserAndRating>? {
        val query =
            "SELECT SUM(A.${UserRating.rating.name}) ${UserRating.rating.name},* FROM ${UserRating.TABLE} A JOIN ${User.TABLE} B on A.${UserRating.reportedUser.name}=B.${User.id.name} GROUP BY ${User.minecraftName.name}"
        val rs = SQLDatabase.connection?.createStatement()?.executeQuery(query)
        return rs?.mapNotNull {
            val reportedUser = User.fromResultSet(it) ?: return@mapNotNull null
            val rating = UserRating.fromResultSet(it) ?: return@mapNotNull null
            UserAndRating(reportedUser, User(), rating)
        }
    }

    suspend fun countPlayerTotalDayRated(player: Player): Int? = catching() {
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} WHERE ${UserRating.userCreatedReport.name}=(SELECT ${User.id.name} FROM ${User.TABLE} WHERE ${User.minecraftName.name}=${player.name.sqlString}) AND (${System.currentTimeMillis()} - ${UserRating.time.name} < ${24 * 60 * 60 * 1000})"
        val rs = SQLDatabase.connection?.createStatement()?.executeQuery(query)
        return@catching rs?.getInt("total")
    }

    suspend fun countPlayerOnPlayerDayRated(player: Player, ratedPlayer: OfflinePlayer): Int? = catching() {
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} " +
                    "WHERE ${UserRating.userCreatedReport.name}=(SELECT ${User.id.name} FROM ${User.TABLE} WHERE ${User.minecraftName.name}=${player.name.sqlString})  " +
                    "AND ${UserRating.reportedUser.name}=(SELECT ${User.id.name} FROM ${User.TABLE} WHERE ${User.minecraftName.name}=${ratedPlayer.name?.sqlString})" +
                    "AND (${System.currentTimeMillis()} - ${UserRating.time.name} < ${24 * 60 * 60 * 1000})"
        val rs = SQLDatabase.connection?.createStatement()?.executeQuery(query)
        return@catching rs?.getInt("total")
    }

}
