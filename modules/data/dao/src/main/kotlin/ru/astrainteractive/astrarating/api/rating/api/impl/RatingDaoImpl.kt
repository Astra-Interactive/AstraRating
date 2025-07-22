package ru.astrainteractive.astrarating.api.rating.api.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrarating.api.rating.api.RatingDao
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.UserModel
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions")
internal class RatingDaoImpl(
    private val databaseFlow: Flow<Database>,
    private val isDebugProvider: () -> Boolean
) : RatingDao, Logger by JUtiltLogger("AstraRating-RatingDBApi") {
    companion object {
        private val MAX_TIMEOUT = 5.seconds
    }

    private fun <T> Result<T>.logFailure(): Result<T> {
        if (!isDebugProvider.invoke()) return this
        onFailure { throwable -> error(throwable) { throwable.message ?: throwable.localizedMessage } }
        return this
    }

    private suspend fun requireDatabase(): Database {
        return databaseFlow.timeout(MAX_TIMEOUT).first()
    }

    override suspend fun selectUser(playerUUID: UUID): Result<UserDTO> = kotlin.runCatching {
        transaction(requireDatabase()) {
            UserTable.selectAll()
                .where { UserTable.minecraftUUID.eq(playerUUID.toString()) }
                .limit(1)
                .map {
                    UserDTO(
                        id = it[UserTable.id].value,
                        minecraftUUID = it[UserTable.minecraftUUID],
                        minecraftName = it[UserTable.minecraftName],
                        lastUpdated = it[UserTable.lastUpdated]
                    )
                }.firstOrNull() ?: error("Could not find user with uuid $playerUUID")
        }
    }.logFailure()

    override suspend fun updateUser(user: UserDTO) = kotlin.runCatching {
        selectUser(user.minecraftUUID.let(UUID::fromString))
        transaction(requireDatabase()) {
            UserTable.update(
                where = { UserTable.minecraftUUID.eq(user.minecraftUUID) },
                body = {
                    it[UserTable.minecraftName] = user.minecraftName
                    it[UserTable.lastUpdated] = user.lastUpdated
                }
            )
        }
    }.logFailure()

    override suspend fun insertUser(user: UserModel) = kotlin.runCatching {
        transaction(requireDatabase()) {
            UserTable.insertAndGetId {
                it[lastUpdated] = System.currentTimeMillis()
                it[minecraftUUID] = user.minecraftUUID.toString()
                it[minecraftName] = user.minecraftName
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
        transaction(requireDatabase()) {
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
        transaction(requireDatabase()) {
            UserRatingTable.deleteWhere { _ ->
                UserRatingTable.id.eq(it.id)
            }
        }
    }.logFailure()

    override suspend fun fetchUserRatings(playerUUID: UUID) = kotlin.runCatching {
        val reportedUser = selectUser(playerUUID).getOrThrow()

        transaction(requireDatabase()) {
            UserRatingTable
                .join(
                    otherTable = UserTable,
                    onColumn = UserRatingTable.userCreatedReport,
                    otherColumn = UserTable.id,
                    joinType = JoinType.INNER,
                )
                .selectAll()
                .where { UserRatingTable.reportedUser.eq(reportedUser.id) }
                .map {
                    UserRatingDTO(
                        id = it[UserTable.id].value,
                        reportedUser = reportedUser,
                        userCreatedReport = UserDTO(
                            id = it[UserTable.id].value,
                            minecraftName = it[UserTable.minecraftName],
                            minecraftUUID = it[UserTable.minecraftUUID],
                            lastUpdated = it[UserTable.lastUpdated]
                        ),
                        time = it[UserRatingTable.time],
                        rating = it[UserRatingTable.rating],
                        ratingType = RatingType.entries
                            .getOrNull(it[UserRatingTable.ratingTypeIndex])
                            ?: RatingType.USER_RATING,
                        message = it[UserRatingTable.message]

                    )
                }
        }
    }.logFailure()

    override suspend fun fetchUsersTotalRating() = kotlin.runCatching {
        transaction(requireDatabase()) {
            val ratingsSum = UserRatingTable.rating.sum().alias("rating_total")
            val ratingsCount = UserRatingTable.rating.count().alias("rating_count")
            val lastUpdated = UserRatingTable.time.max().alias("last_updated")

            UserTable
                .join(
                    otherTable = UserRatingTable,
                    onColumn = UserTable.id,
                    otherColumn = UserRatingTable.reportedUser,
                    joinType = JoinType.INNER,
                )
                .select(
                    UserTable.id,
                    UserTable.minecraftUUID,
                    UserTable.minecraftName,
                    ratingsSum,
                    ratingsCount,
                    lastUpdated
                )
                .groupBy(UserTable.id)
                .map {
                    RatedUserDTO(
                        userDTO = UserDTO(
                            id = it[UserTable.id].value,
                            minecraftUUID = it[UserTable.minecraftUUID],
                            minecraftName = it[UserTable.minecraftName],
                            lastUpdated = it[lastUpdated] ?: 0L,
                        ),
                        ratingTotal = it[ratingsSum] ?: 0,
                        ratingCounts = it[ratingsCount]
                    )
                }
        }
    }.logFailure()

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID) = kotlin.runCatching {
        val user = selectUser(playerUUID).getOrThrow()
        transaction(requireDatabase()) {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport.eq(user.id).and {
                        UserRatingTable.time.greater(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    }
                }.count()
        }
    }.logFailure()

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID) = kotlin.runCatching {
        val playerCreatedReport = selectUser(playerUUID).getOrThrow()
        val ratedPlayer = selectUser(ratedPlayerUUID).getOrThrow()
        transaction(requireDatabase()) {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport
                        .eq(playerCreatedReport.id)
                        .and { UserRatingTable.reportedUser.eq(ratedPlayer.id) }
                }.count()
        }
    }.logFailure()
}
