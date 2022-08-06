package com.astrainteractive.astrarating.api

import com.astrainteractive.astralibs.database.*
import com.astrainteractive.astralibs.utils.mapNotNull
import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.sqldatabase.*
import com.astrainteractive.astrarating.utils.uuid
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.sql.Connection

object DatabaseApi {
    val database: SQLDatabase?
        get() = SQLDatabase.instance
    val connection: Connection?
        get() = database?.connection

    private val userEntityInfo by lazy {
        AnnotationUtils.EntityInfo.create(User::class.java)
    }
    private val userRatingEntityInfo by lazy {
        AnnotationUtils.EntityInfo.create(UserRating::class.java)
    }

    suspend fun selectUser(it:OfflinePlayer) = database?.select<User>("WHERE ${User::minecraftName.columnName}=${it.name?.uppercase()?.sqlString}")?.firstOrNull()
    suspend fun updateUser(it:User) = database?.update(it)
    suspend fun insertUser(it: User) = database?.insert(it.copy(minecraftName = it.minecraftName.uppercase()),true)
    suspend fun insertUserRating(it: UserRating) = database?.insert(it)
    suspend fun deleteUserRating(it: UserRating) = database?.delete(it)

    suspend fun fetchUserRatings(player: OfflinePlayer): List<UserAndRating>? = catching(true) {
        val subSelectQuery =
            "(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${player.name?.uppercase()?.sqlString} LIMIT 1)"
        val joinQuery =
            "JOIN ${User.TABLE} B on A.${UserRating::userCreatedReport.columnName}=B.${User::id.columnName} WHERE A.${UserRating::reportedUser.columnName}=$subSelectQuery"
        val query =
            "SELECT * FROM ${UserRating.TABLE} A $joinQuery"
        val reportedUser =
            database?.select<User>("WHERE ${User::minecraftName.columnName}=${player.name?.uppercase()?.sqlString}")?.firstOrNull()
                ?: return null
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.mapNotNull {
            UserAndRating(
                reportedUser,
                fromResultSet(User::class.java, userEntityInfo, it) ?: return@mapNotNull null,
                fromResultSet(UserRating::class.java, userRatingEntityInfo, it) ?: return@mapNotNull null,
            )
        }
    }

    suspend fun fetchUsersTotalRating(): List<UserAndRating>? = catching(true) {
        val joinQuery =
            "JOIN ${User.TABLE} B on A.${UserRating::reportedUser.columnName}=B.${User::id.columnName} GROUP BY ${User::minecraftName.columnName}"
        val query =
            "SELECT SUM(A.${UserRating::rating.columnName}) ${UserRating::rating.columnName},* FROM ${UserRating.TABLE} A $joinQuery"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.mapNotNull {
            UserAndRating(
                fromResultSet(User::class.java, userEntityInfo, it) ?: return@mapNotNull null,
                User(NON_EXISTS_KEY,"","","",System.currentTimeMillis()),
                fromResultSet(UserRating::class.java, userRatingEntityInfo, it) ?: return@mapNotNull null,
            )
        }
    }

    suspend fun countPlayerTotalDayRated(player: Player): Int? = catching(true) {
        val nestedSelectQuery =
            "(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${player.name.uppercase().sqlString}) AND (${System.currentTimeMillis()} - ${UserRating::time.columnName} < ${24 * 60 * 60 * 1000})"
        val whereQuery = "WHERE ${UserRating::userCreatedReport.columnName}=$nestedSelectQuery"
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} $whereQuery"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.getInt("total")
    }

    suspend fun countPlayerOnPlayerDayRated(player: Player, ratedPlayer: OfflinePlayer): Int? = catching(true) {
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} " +
                    "WHERE ${UserRating::userCreatedReport.columnName}=(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${player.name.uppercase().sqlString})  " +
                    "AND ${UserRating::reportedUser.columnName}=(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${ratedPlayer.name?.uppercase()?.sqlString})" +
                    "AND (${System.currentTimeMillis()} - ${UserRating::time.columnName} < ${24 * 60 * 60 * 1000})"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.getInt("total")
    }
}