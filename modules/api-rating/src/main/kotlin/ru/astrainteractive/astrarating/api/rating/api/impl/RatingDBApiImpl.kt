package ru.astrainteractive.astrarating.api.rating.api.impl

import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.firstOrNull
import ru.astrainteractive.astralibs.orm.mapNotNull
import ru.astrainteractive.astralibs.orm.query.CountQuery
import ru.astrainteractive.astralibs.orm.query.SelectQuery
import ru.astrainteractive.astralibs.orm.with
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.db.rating.entity.UserEntity
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingEntity
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.db.rating.mapping.UserMapper
import ru.astrainteractive.astrarating.db.rating.mapping.UserRatingMapper
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserAndRating
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.UserModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

class RatingDBApiImpl(private val database: Database, private val pluginFolder: File) :
    RatingDBApi {
    private fun <T> Result<T>.logStackTrace(): Result<T> {
        return this.onFailure {
            val logsFolder = File(pluginFolder, "logs")
            if (!logsFolder.exists()) logsFolder.mkdirs()
            val fileName = SimpleDateFormat("dd.MM.yyyy").format(Date.from(Instant.now()))
            val logFile = File(logsFolder, "$fileName.log")
            if (!logFile.exists()) logFile.createNewFile()
            logFile.appendText(it.stackTraceToString() + "\n")
        }
    }

    private val String.sqlString: String
        get() = "\"$this\""

    override suspend fun selectUser(playerName: String): Result<UserDTO> = kotlin.runCatching {
        UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.map(UserMapper::toDTO).first()
    }.logStackTrace()

    override suspend fun updateUser(user: UserDTO) = kotlin.runCatching {
        val userEntity = UserTable.find(database, constructor = UserEntity) {
            UserTable.id.eq(user.id)
        }.firstOrNull()
        userEntity?.lastUpdated = System.currentTimeMillis()
        user.discordID?.let { userEntity?.discordID = it }
        userEntity?.let { UserTable.update(database, entity = it) }
    }.logStackTrace()

    override suspend fun insertUser(user: UserModel) = kotlin.runCatching {
        UserTable.insert(database) {
            this[UserTable.lastUpdated] = System.currentTimeMillis()
            this[UserTable.minecraftUUID] = user.minecraftUUID.toString()
            this[UserTable.minecraftName] = user.minecraftName.uppercase()
            this[UserTable.discordID] = null
        }
    }.logStackTrace()

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
    }.logStackTrace()

    override suspend fun deleteUserRating(it: UserRatingDTO) = kotlin.runCatching {
        UserRatingTable.delete<UserRatingEntity>(database) {
            UserRatingTable.id.eq(it.id)
        }
    }.logStackTrace()

    override suspend fun fetchUserRatings(playerName: String) = kotlin.runCatching {
        val query = """
            SELECT * FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.userCreatedReport.name}=B.${UserTable.id.name} WHERE A.${UserRatingTable.reportedUser.name}=
            (SELECT ${UserTable.id.name} FROM ${UserTable.tableName} WHERE ${UserTable.minecraftName.name}=${playerName.sqlString?.uppercase()} LIMIT 1)
        """.trimIndent()
        val reportedUser = UserTable.find(database, constructor = UserEntity) {
            UserTable.minecraftName.eq(playerName.uppercase())
        }.firstOrNull()?.let(UserMapper::toDTO)
        checkNotNull(reportedUser) { "Reported user not found" }
        val statement = database.connection?.createStatement()
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
    }.logStackTrace()

    override suspend fun fetchUsersTotalRating() = kotlin.runCatching {
        val query = """
            SELECT *, SUM(A.${UserRatingTable.rating.name}) rating__ FROM ${UserRatingTable.tableName} A 
            JOIN ${UserTable.tableName} B on A.${UserRatingTable.reportedUser.name}=B.${UserTable.id.name} GROUP BY ${UserTable.minecraftName.name}
        """.trimIndent()
        val statement = database.connection?.createStatement()
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
    }.logStackTrace()

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
    }.logStackTrace()

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
    }.logStackTrace()
}
