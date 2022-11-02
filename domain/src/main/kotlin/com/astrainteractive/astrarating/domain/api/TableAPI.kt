package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.domain.SQLDatabase
import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserAndRating
import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.domain.entities.tables.UserEntity
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingEntity
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import com.astrainteractive.astrarating.domain.entities.tables.dto.mapping.UserMapper
import com.astrainteractive.astrarating.domain.entities.tables.dto.mapping.UserRatingMapper
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.columnName
import ru.astrainteractive.astralibs.database.fromResultSet
import ru.astrainteractive.astralibs.database.sqlString
import ru.astrainteractive.astralibs.database_v2.Database
import ru.astrainteractive.astralibs.utils.mapNotNull


class TableAPI(private val database: Database) : IRatingAPI {
    override suspend fun selectUser(playerName: String): User? {
        return UserTable.find(constructor = ::UserEntity) {
            UserTable.minecraftName.eq(playerName)
        }.map(UserMapper::toDTO).firstOrNull()
    }

    override suspend fun updateUser(user: User) {
        val userEntity = UserTable.find(constructor = ::UserEntity) {
            UserTable.id.eq(user.id)
        }.firstOrNull()
        userEntity?.lastUpdated = user.lastUpdated
        user.discordID?.let { userEntity?.discordID = it }
        userEntity?.let { UserTable.update(entity = it) }
    }

    override suspend fun insertUser(user: User): Long? {
        return UserTable.insert {
            this[UserTable.lastUpdated] = user.lastUpdated
            this[UserTable.minecraftUUID] = user.minecraftUUID
            this[UserTable.minecraftName] = user.minecraftName
            this[UserTable.discordID] = user.discordID
        }.toLong()
    }

    override suspend fun insertUserRating(it: UserRating): Long? {
        return UserRatingTable.insert {
            this[UserRatingTable.userCreatedReport] = it.userCreatedReport
            this[UserRatingTable.reportedUser] = it.reportedUser
            this[UserRatingTable.rating] = it.rating
            this[UserRatingTable.message] = it.message
            this[UserRatingTable.time] = it.time
        }.toLong()
    }

    override suspend fun deleteUserRating(it: UserRating) {
        UserRatingTable.delete<UserRatingEntity> {
            UserRatingTable.id.eq(it.id)
        }
    }

    override suspend fun fetchUserRatings(playerName: String): List<UserAndRating>? {
        val query = """
            SELECT * FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.userCreatedReport.name}=B.${UserTable.id.name} WHERE A.${UserRatingTable.reportedUser.name}=
            (SELECT ${UserTable.id.name} FROM ${UserTable.tableName} WHERE ${UserTable.minecraftName.name}=${playerName?.sqlString} LIMIT 1)
        """.trimIndent()
        val reportedUser = UserTable.find(constructor = ::UserEntity){
            UserTable.minecraftName.eq(playerName)
        }.firstOrNull()?.let(UserMapper::toDTO)!!
        val rs = database?.connection?.createStatement()?.executeQuery(query)
        return rs?.mapNotNull {
            UserAndRating(
                reportedUser,
                UserTable.wrap(it,::UserEntity).let(UserMapper::toDTO),
                UserRatingTable.wrap(it,::UserRatingEntity).let(UserRatingMapper::toDTO),
            )
        }
    }

    override suspend fun fetchUsersTotalRating(): List<UserAndRating>? {
        val query = """
            SELECT SUM(A.${UserRatingTable.rating.name}) ${UserRatingTable.rating.name},* FROM ${UserRating.TABLE} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.reportedUser.name}=B.${UserTable.id.name} GROUP BY ${UserTable.minecraftName.name}
        """.trimIndent()
        val rs = database?.connection?.createStatement()?.executeQuery(query)
        return rs?.mapNotNull {
            UserAndRating(
                UserTable.wrap(it,::UserEntity).let(UserMapper::toDTO),
                User(SQLDatabase.NON_EXISTS_KEY, "", "", "", System.currentTimeMillis()),
                UserRatingTable.wrap(it,::UserRatingEntity).let(UserRatingMapper::toDTO),
            )
        }
    }

    override suspend fun countPlayerTotalDayRated(playerName: String): Int? {
        val query = """
            SELECT COUNT(*) total FROM ${UserRatingTable.tableName} 
            WHERE ${UserRatingTable.userCreatedReport.name}=
              (SELECT ${UserRatingTable.id.name} FROM ${UserTable.tableName} WHERE ${UserTable.minecraftName.name}=${playerName.sqlString}) AND (${System.currentTimeMillis()} - ${UserRating::time.columnName} < ${24 * 60 * 60 * 1000})
        """.trimIndent()
        val rs = database.connection?.createStatement()?.executeQuery(query)
        return rs?.getInt("total") ?: 0
    }

    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String): Int? {
        val query =
            """
                    SELECT COUNT(*) total FROM ${UserRatingTable.tableName} 
                    WHERE ${UserRatingTable.userCreatedReport.name} = 
                      (SELECT ${UserTable.id.name} FROM ${User.TABLE} WHERE ${UserTable.minecraftName.name}=${playerName.sqlString})   
                    AND ${UserRatingTable.reportedUser.name} = 
                      (SELECT ${UserTable.id.name} FROM ${User.TABLE} WHERE ${UserTable.minecraftName.name}=${ratedPlayerName?.sqlString}) 
                    AND (${System.currentTimeMillis()} - ${UserRatingTable.time.name} < ${24 * 60 * 60 * 1000})
                    """
        val rs = database.connection?.createStatement()?.executeQuery(query)
        return rs?.getInt("total") ?: 0
    }
}