package ru.astrainteractive.astrarating.api.rating.api.impl

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
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
import ru.astrainteractive.klibs.kdi.Provider

internal class RatingDBApiImpl(
    private val database: Database,
    private val isDebugProvider: Provider<Boolean>
) : RatingDBApi, Logger by JUtiltLogger("RatingDBApi") {

    private fun <T> Result<T>.logFailure(): Result<T> {
        if (!isDebugProvider.provide()) return this
        onFailure { throwable -> error(throwable) { throwable.message ?: throwable.localizedMessage } }
        return this
    }

    override suspend fun selectUser(playerName: String): Result<UserDTO> = kotlin.runCatching {
        transaction(database) {
            UserDAO.find {
                UserTable.minecraftName.eq(playerName.uppercase())
                    .or { UserTable.minecraftName.eq(playerName) }
            }.firstOrNull()?.let(UserMapper::toDTO) ?: error("Could not find $playerName")
        }
    }.logFailure()

    override suspend fun updateUser(user: UserDTO) = kotlin.runCatching {
        transaction(database) {
            val userDao = UserDAO.findById(user.id) ?: error("User not found!")
            userDao.lastUpdated = System.currentTimeMillis()
        }
    }.logFailure()

    override suspend fun insertUser(user: UserModel) = kotlin.runCatching {
        transaction(database) {
            UserTable.insertAndGetId {
                it[lastUpdated] = System.currentTimeMillis()
                it[minecraftUUID] = user.minecraftUUID.toString()
                it[minecraftName] = user.minecraftName
                it[discordID] = null
            }.value
        }
    }.logFailure()

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
    }.logFailure()

    override suspend fun deleteUserRating(it: UserRatingDTO) = kotlin.runCatching {
        transaction(database) {
            UserRatingTable.deleteWhere { _ ->
                UserRatingTable.id.eq(it.id)
            }
        }
    }.logFailure()

    override suspend fun fetchUserRatings(playerName: String) = kotlin.runCatching {
        val reportedUser = selectUser(playerName).getOrThrow()
        transaction(database) {
            UserRatingDAO.find {
                UserRatingTable.reportedUser.eq(reportedUser.id)
            }.map(UserRatingMapper::toDTO)
        }
    }.logFailure()

    override suspend fun fetchUsersTotalRating() = kotlin.runCatching {
        transaction(database) {
            UserDAO.all().filter { !it.rating.empty() }.map {
                val reportedPlayer = it.let(UserMapper::toDTO)
                RatedUserDTO(
                    userDTO = reportedPlayer,
                    rating = it.rating.sumOf { it.rating }
                )
            }
        }
    }.logFailure()

    override suspend fun countPlayerTotalDayRated(playerName: String) = kotlin.runCatching {
        val user = selectUser(playerName).getOrThrow()
        transaction(database) {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport.eq(user.id).and {
                        UserRatingTable.time.greater(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    }
                }.count()
        }
    }.logFailure()

    override suspend fun countPlayerOnPlayerDayRated(playerName: String, ratedPlayerName: String) = kotlin.runCatching {
        val playerCreatedReport = selectUser(playerName).getOrThrow()
        val ratedPlayer = selectUser(ratedPlayerName).getOrThrow()
        transaction(database) {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport
                        .eq(playerCreatedReport.id)
                        .and { UserRatingTable.reportedUser.eq(ratedPlayer.id) }
                }.count()
        }
    }.logFailure()
}
