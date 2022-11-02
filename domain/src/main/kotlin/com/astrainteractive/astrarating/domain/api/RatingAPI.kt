package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.domain.SQLDatabase.Companion.NON_EXISTS_KEY
import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserAndRating
import com.astrainteractive.astrarating.domain.entities.UserRating
import ru.astrainteractive.astralibs.database.*
import ru.astrainteractive.astralibs.utils.mapNotNull
import ru.astrainteractive.astralibs.utils.catching
import java.sql.Connection


class RatingAPI(private val database: DatabaseCore) : IRatingAPI {
    val connection: Connection?
        get() = database.connection

    private val userEntityInfo by lazy {
        AnnotationUtils.EntityInfo.create(User::class.java)
    }
    private val userRatingEntityInfo by lazy {
        AnnotationUtils.EntityInfo.create(UserRating::class.java)
    }

    override suspend fun selectUser(playerName: String) =
        database.select<User>("WHERE ${User::minecraftName.columnName}=${playerName?.uppercase()?.sqlString}")
            ?.firstOrNull()

    override suspend fun updateUser(user: User){
        database.update(user)
    }
    override suspend fun insertUser(user: User) =
        database.insert(user.copy(minecraftName = user.minecraftName.uppercase()), true)
    override suspend fun insertUserRating(it: UserRating): Long? {
        return database.insert(it)
    }
    override suspend fun deleteUserRating(it: UserRating){
        database.delete(it)
    }

    override suspend fun fetchUserRatings(playerName: String): List<UserAndRating>? = catching(true) {
        val subSelectQuery =
            "(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${playerName.uppercase()?.sqlString} LIMIT 1)"
        val joinQuery =
            "JOIN ${User.TABLE} B on A.${UserRating::userCreatedReport.columnName}=B.${User::id.columnName} WHERE A.${UserRating::reportedUser.columnName}=$subSelectQuery"
        val query =
            "SELECT * FROM ${UserRating.TABLE} A $joinQuery"
        val reportedUser =
            database.select<User>("WHERE ${User::minecraftName.columnName}=${playerName.uppercase().sqlString}")
                ?.firstOrNull()
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

    override suspend fun fetchUsersTotalRating(): List<UserAndRating>? = catching(true) {
        val joinQuery =
            "JOIN ${User.TABLE} B on A.${UserRating::reportedUser.columnName}=B.${User::id.columnName} GROUP BY ${User::minecraftName.columnName}"
        val query =
            "SELECT SUM(A.${UserRating::rating.columnName}) ${UserRating::rating.columnName},* FROM ${UserRating.TABLE} A $joinQuery"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.mapNotNull {
            UserAndRating(
                fromResultSet(User::class.java, userEntityInfo, it) ?: return@mapNotNull null,
                User(NON_EXISTS_KEY, "", "", "", System.currentTimeMillis()),
                fromResultSet(UserRating::class.java, userRatingEntityInfo, it) ?: return@mapNotNull null,
            )
        }
    }

    override suspend fun countPlayerTotalDayRated(playerName: String): Int? = catching(true) {
        val nestedSelectQuery =
            "(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${playerName.uppercase().sqlString}) AND (${System.currentTimeMillis()} - ${UserRating::time.columnName} < ${24 * 60 * 60 * 1000})"
        val whereQuery = "WHERE ${UserRating::userCreatedReport.columnName}=$nestedSelectQuery"
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} $whereQuery"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.getInt("total")
    }

    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String): Int? = catching(true) {
        val query =
            "SELECT COUNT(*) total FROM ${UserRating.TABLE} " +
                    "WHERE ${UserRating::userCreatedReport.columnName}=(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${playerName.uppercase().sqlString})  " +
                    "AND ${UserRating::reportedUser.columnName}=(SELECT ${User::id.columnName} FROM ${User.TABLE} WHERE ${User::minecraftName.columnName}=${ratedPlayerName?.uppercase()?.sqlString})" +
                    "AND (${System.currentTimeMillis()} - ${UserRating::time.columnName} < ${24 * 60 * 60 * 1000})"
        val rs = connection?.createStatement()?.executeQuery(query)
        rs?.getInt("total")
    }
}