package com.astrainteractive.astrarating.domain.api.impl

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.entities.UserEntity
import com.astrainteractive.astrarating.domain.entities.UserRatingEntity
import com.astrainteractive.astrarating.domain.entities.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.UserTable
import com.astrainteractive.astrarating.domain.mapping.UserMapper
import com.astrainteractive.astrarating.domain.mapping.UserRatingMapper
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.dto.UserAndRating
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.dto.UserRatingDTO
import com.astrainteractive.astrarating.models.UserModel
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.firstOrNull
import ru.astrainteractive.astralibs.orm.mapNotNull
import ru.astrainteractive.astralibs.orm.query.CountQuery
import ru.astrainteractive.astralibs.orm.query.SelectQuery
import ru.astrainteractive.astralibs.orm.with

class RatingDBApiImpl(private val database: Database) : RatingDBApi {
    private val String.sqlString: String
        get() = "\"$this\""

    override suspend fun selectUser(playerName: String): Result<UserDTO> = kotlin.runCatching {
        UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.map(UserMapper::toDTO).first()
    }

    override suspend fun updateUser(user: UserDTO) = kotlin.runCatching {
        val userEntity = UserTable.find(database, constructor = UserEntity) {
            UserTable.id.eq(user.id)
        }.firstOrNull()
        userEntity?.lastUpdated = System.currentTimeMillis()
        user.discordID?.let { userEntity?.discordID = it }
        userEntity?.let { UserTable.update(database, entity = it) }
    }

    override suspend fun insertUser(user: UserModel) = kotlin.runCatching {
        UserTable.insert(database) {
            this[UserTable.lastUpdated] = System.currentTimeMillis()
            this[UserTable.minecraftUUID] = user.minecraftUUID.toString()
            this[UserTable.minecraftName] = user.minecraftName.uppercase()
            this[UserTable.discordID] = user.discordID
        }
    }

    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ) = kotlin.runCatching {
        UserRatingTable.insert(database) {
            this[UserRatingTable.userCreatedReport] = reporter?.id
            this[UserRatingTable.reportedUser] = reported.id
            this[UserRatingTable.rating] = ratingValue
            this[UserRatingTable.message] = message
            this[UserRatingTable.time] = System.currentTimeMillis()
            this[UserRatingTable.ratingTypeIndex] = type.ordinal
        }
    }

    override suspend fun deleteUserRating(it: UserRatingDTO) = kotlin.runCatching {
        UserRatingTable.delete<UserRatingEntity>(database) {
            UserRatingTable.id.eq(it.id)
        }
    }

    override suspend fun fetchUserRatings(playerName: String) = kotlin.runCatching {
        val query = """
            SELECT * FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.userCreatedReport.name}=B.${UserTable.id.name} WHERE A.${UserRatingTable.reportedUser.name}=
            (SELECT ${UserTable.id.name} FROM ${UserTable.tableName} WHERE ${UserTable.minecraftName.name}=${playerName?.sqlString?.uppercase()} LIMIT 1)
        """.trimIndent()
        val reportedUser = UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.firstOrNull()?.let(UserMapper::toDTO)
        checkNotNull(reportedUser) { "Reported user not found" }
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
        result ?: emptyList()
    }

    override suspend fun fetchUsersTotalRating() = kotlin.runCatching {
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
                UserDTO(-1, "", "", "", System.currentTimeMillis()),
                UserRatingTable.wrap(it, UserRatingEntity).let(UserRatingMapper::toDTO).copy(rating = rating),
            )
        }
        statement?.close()
        result ?: emptyList()
    }

    override suspend fun countPlayerTotalDayRated(playerName: String) = kotlin.runCatching {
        val query = CountQuery(UserRatingTable) {
            UserRatingTable.userCreatedReport.eq {
                SelectQuery(UserTable, UserTable.id) {
                    UserTable.minecraftName.eq(playerName.uppercase()).and {
                        UserRatingTable.time.more(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    }
                }
            }
        }.generate()
        database.connection?.createStatement()?.with {
            executeQuery(query)?.firstOrNull {
                it.getInt("total")
            } ?: 0
        } ?: 0
    }

    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String) = kotlin.runCatching {
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
        database.connection?.createStatement()?.with {
            executeQuery(query)?.firstOrNull {
                it.getInt("total")
            } ?: 0
        } ?: 0
    }
}
