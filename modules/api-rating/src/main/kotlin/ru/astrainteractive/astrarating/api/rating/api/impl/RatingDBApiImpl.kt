package ru.astrainteractive.astrarating.api.rating.api.impl

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.db.rating.entity.UserDAO
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingDAO
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.db.rating.mapping.UserMapper
import ru.astrainteractive.astrarating.db.rating.mapping.UserRatingMapper
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.UserModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

internal class RatingDBApiImpl(
    private val database: Database,
    private val pluginFolder: File
) : RatingDBApi {
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

    override suspend fun selectUser(playerName: String): Result<UserDTO> = kotlin.runCatching {
        transaction(database) {
            UserDAO.find {
                UserTable.minecraftName.eq(playerName.uppercase())
            }.first().let(UserMapper::toDTO)
        }
    }.logStackTrace()

    override suspend fun updateUser(user: UserDTO) = kotlin.runCatching {
        transaction(database) {
            val userDao = UserDAO.findById(user.id) ?: error("User not found!")
            userDao.lastUpdated = System.currentTimeMillis()
        }
    }.logStackTrace()

    override suspend fun insertUser(user: UserModel) = kotlin.runCatching {
        transaction(database) {
            UserTable.insertAndGetId {
                it[lastUpdated] = System.currentTimeMillis()
                it[minecraftUUID] = user.minecraftUUID.toString()
                it[minecraftName] = user.minecraftName.uppercase()
                it[discordID] = null
            }.value
        }
    }.logStackTrace()

    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ) = kotlin.runCatching {
        transaction(database) {
            UserRatingTable.insertAndGetId {
                it[userCreatedReport] = reporter?.id
                it[reportedUser] = reported.id
                it[rating] = ratingValue
                it[UserRatingTable.message] = message
                it[time] = System.currentTimeMillis()
                it[ratingTypeIndex] = type.ordinal
            }.value
        }
    }.logStackTrace()

    override suspend fun deleteUserRating(it: UserRatingDTO) = kotlin.runCatching {
        transaction(database) {
            UserRatingTable.deleteWhere { _ ->
                UserRatingTable.id.eq(it.id)
            }
        }
    }.logStackTrace()

    override suspend fun fetchUserRatings(playerName: String) = kotlin.runCatching {
        val reportedUser = selectUser(playerName).getOrThrow()
        transaction(database) {
            UserRatingDAO.find {
                UserRatingTable.reportedUser.eq(reportedUser.id)
            }.map(UserRatingMapper::toDTO)
        }
    }.logStackTrace()

    override suspend fun fetchUsersTotalRating() = kotlin.runCatching {
        transaction(database) {
            UserDAO.all().map {
                val reportedPlayer = it.let(UserMapper::toDTO)
                RatedUserDTO(
                    userDTO = reportedPlayer,
                    rating = it.rating.sumOf { it.rating }
                )
            }
        }
    }.logStackTrace()

    override suspend fun countPlayerTotalDayRated(playerName: String) = kotlin.runCatching {
        transaction(database) {
            UserTable.select {
                UserTable.minecraftName.eq(playerName.uppercase()).and {
                    UserRatingTable.time.greater(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                }
            }.count()
        }
    }.logStackTrace()

    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String) = kotlin.runCatching {
        val playerCreatedReport = selectUser(playerName).getOrThrow()
        val ratedPlayer = selectUser(ratedPlayerName).getOrThrow()
        transaction(database) {
            UserRatingTable.select {
                UserRatingTable.userCreatedReport
                    .eq(playerCreatedReport.id)
                    .and { UserRatingTable.reportedUser.eq(ratedPlayer.id) }
            }.count()
        }
    }.logStackTrace()
}
