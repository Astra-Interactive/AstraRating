package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.domain.entities.tables.UserEntity
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingEntity
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserAndRating
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.mapping.UserMapper
import com.astrainteractive.astrarating.domain.entities.tables.dto.mapping.UserRatingMapper
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.firstOrNull
import ru.astrainteractive.astralibs.orm.query.CountQuery
import ru.astrainteractive.astralibs.orm.query.SelectQuery
import ru.astrainteractive.astralibs.orm.with
import ru.astrainteractive.astralibs.utils.mapNotNull
import java.sql.Statement


class TableAPI(private val database: Database) : IRatingAPI {
    private val String.sqlString: String
        get() = "\"$this\""

    override suspend fun selectUser(playerName: String): UserDTO? {
        return UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.map(UserMapper::toDTO).firstOrNull()
    }

    override suspend fun updateUser(user: UserDTO) {
        val userEntity = UserTable.find(database, constructor = UserEntity) {
            UserTable.id.eq(user.id)
        }.firstOrNull()
        userEntity?.lastUpdated = System.currentTimeMillis()
        user.discordID?.let { userEntity?.discordID = it }
        userEntity?.let { UserTable.update(database, entity = it) }
    }

    override suspend fun insertUser(user: UserDTO): Int? {
        return UserTable.insert(database) {
            this[UserTable.lastUpdated] = System.currentTimeMillis()
            this[UserTable.minecraftUUID] = user.minecraftUUID
            this[UserTable.minecraftName] = user.minecraftName.uppercase()
            this[UserTable.discordID] = user.discordID
        }
    }

    override suspend fun insertUserRating(it: UserRatingDTO): Int? {
        return UserRatingTable.insert(database) {
            this[UserRatingTable.userCreatedReport] = it.userCreatedReport
            this[UserRatingTable.reportedUser] = it.reportedUser
            this[UserRatingTable.rating] = it.rating
            this[UserRatingTable.message] = it.message
            this[UserRatingTable.time] = System.currentTimeMillis()
            this[UserRatingTable.ratingTypeIndex] = it.ratingType.ordinal
        }
    }

    override suspend fun deleteUserRating(it: UserRatingDTO) {
        UserRatingTable.delete<UserRatingEntity>(database) {
            UserRatingTable.id.eq(it.id)
        }
    }

    override suspend fun fetchUserRatings(playerName: String): List<UserAndRating>? {
        val query = """
            SELECT * FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.userCreatedReport.name}=B.${UserTable.id.name} WHERE A.${UserRatingTable.reportedUser.name}=
            (SELECT ${UserTable.id.name} FROM ${UserTable.tableName} WHERE ${UserTable.minecraftName.name}=${playerName?.sqlString?.uppercase()} LIMIT 1)
        """.trimIndent()
        val reportedUser = UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.firstOrNull()?.let(UserMapper::toDTO) ?: return null
        val statement = database?.connection?.createStatement()
        val rs = statement?.executeQuery(query)
        val result = rs?.mapNotNull {
            UserAndRating(
                reportedUser,
                UserTable.wrap(it, UserEntity).let(UserMapper::toDTO),
                UserRatingTable.wrap(it, UserRatingEntity).let(UserRatingMapper::toDTO),
            )
        }
        statement?.close()
        return result
    }

    override suspend fun fetchUsersTotalRating(): List<UserAndRating>? {
        val query = """
            SELECT *, SUM(A.${UserRatingTable.rating.name}) rating__ FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.reportedUser.name}=B.${UserTable.id.name} GROUP BY ${UserTable.minecraftName.name}
        """.trimIndent()
        val statement = database?.connection?.createStatement()
        val rs = statement?.executeQuery(query)
        val result = rs?.mapNotNull {
            val rating = it.getInt("rating__")
            UserAndRating(
                UserTable.wrap(it, UserEntity).let(UserMapper::toDTO),
                UserDTO(NON_EXISTS_KEY, "", "", "", System.currentTimeMillis()),
                UserRatingTable.wrap(it, UserRatingEntity).let(UserRatingMapper::toDTO).copy(rating = rating),
            )
        }
        statement?.close()
        return result
    }

    override suspend fun countPlayerTotalDayRated(playerName: String): Int? {
        val query = CountQuery(UserRatingTable) {
            UserRatingTable.userCreatedReport.eq {
                SelectQuery(UserTable, UserTable.id) {
                    UserTable.minecraftName.eq(playerName.uppercase()).and {
                        UserRatingTable.time.more(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    }
                }
            }
        }.generate()
        return database.connection?.createStatement()?.with {
            executeQuery(query)?.firstOrNull {
                it.getInt("total")
            } ?: 0
        }
    }


    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String): Int? {
        val query = CountQuery(UserRatingTable) {
            UserRatingTable.userCreatedReport.eq {
                SelectQuery(UserTable, UserTable.id) {
                    UserTable.minecraftName.eq(playerName.uppercase())
                }
            }.andQuery {
                SelectQuery(UserTable, UserTable.id) {
                    UserTable.minecraftName.eq(ratedPlayerName.uppercase())
                }
            }.and(UserRatingTable.time.more(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
        }.generate()
        return database.connection?.createStatement()?.with {
            executeQuery(query)?.firstOrNull {
                it.getInt("total")
            } ?: 0
        }
    }
}