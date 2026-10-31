package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.core.sum
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingStorageError
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.data.exposed.table.UserRatingTable
import ru.astrainteractive.astrarating.data.exposed.table.UserTable
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID
import kotlin.Result.Companion.success
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Suppress("TooManyFunctions")
internal class ExposedRatingRepository(
    private val databaseFlow: Flow<Database>,
    private val clock: Clock,
    private val isDebugProvider: () -> Boolean
) : RatingRepository, Logger by JUtiltLogger("AstraRating-RatingDBApi") {
    private fun <T> Result<T>.logFailure(): Result<T> {
        if (!isDebugProvider.invoke()) return this
        onFailure { throwable -> error(throwable) { throwable.message ?: throwable.localizedMessage } }
        return this
    }

    private suspend fun <T> query(statement: JdbcTransaction.() -> T): Result<T> {
        val database = withTimeoutOrNull(MAX_TIMEOUT) { databaseFlow.first() }
            ?: return Result.failure<T>(RatingStorageError.DatabaseUnavailable()).logFailure()
        return runCatching {
            transaction(database) {
                maxAttempts = 1
                statement.invoke(this)
            }
        }
            .propagateCancellationException()
            .fold(
                onSuccess = ::success,
                onFailure = { error -> Result.failure<T>(RatingStorageError.QueryFailed(error)) }
            )
            .logFailure()
    }

    private fun userOf(row: ResultRow): User {
        return User(
            id = row[UserTable.id].value,
            minecraftUUID = row[UserTable.minecraftUUID],
            minecraftName = row[UserTable.minecraftName],
            lastUpdated = Instant.fromEpochMilliseconds(row[UserTable.lastUpdated])
        )
    }

    private fun userRatingOf(row: ResultRow, reportedUser: User): UserRating {
        return UserRating(
            id = row[UserRatingTable.id].value,
            reportedUser = reportedUser,
            userCreatedReport = row[UserRatingTable.userCreatedReport]?.let { reporterId ->
                User(
                    id = reporterId.value,
                    minecraftName = row[UserTable.minecraftName],
                    minecraftUUID = row[UserTable.minecraftUUID],
                    lastUpdated = Instant.fromEpochMilliseconds(row[UserTable.lastUpdated])
                )
            },
            time = Instant.fromEpochMilliseconds(row[UserRatingTable.time]),
            rating = row[UserRatingTable.rating],
            ratingType = RatingType.entries
                .getOrNull(row[UserRatingTable.ratingTypeIndex])
                ?: RatingType.USER_RATING,
            message = row[UserRatingTable.message]
        )
    }

    override suspend fun findUser(playerUUID: UUID): Result<User?> {
        return query {
            UserTable.selectAll()
                .where { UserTable.minecraftUUID.eq(playerUUID.toString()) }
                .limit(1)
                .map(::userOf)
                .firstOrNull()
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return query {
            UserTable.update(
                where = { UserTable.minecraftUUID.eq(user.minecraftUUID) },
                body = { statement ->
                    statement[UserTable.minecraftName] = user.minecraftName
                    statement[UserTable.lastUpdated] = user.lastUpdated.toEpochMilliseconds()
                }
            )
        }.map { _ -> }
    }

    override suspend fun insertUser(user: NewUser): Result<User> {
        val lastUpdated = Instant.fromEpochMilliseconds(clock.now().toEpochMilliseconds())
        return query {
            UserTable.insertAndGetId { statement ->
                statement[UserTable.lastUpdated] = lastUpdated.toEpochMilliseconds()
                statement[UserTable.minecraftUUID] = user.minecraftUUID.toString()
                statement[UserTable.minecraftName] = user.minecraftName
            }.value
        }.map { id ->
            User(
                id = id,
                minecraftUUID = user.minecraftUUID.toString(),
                minecraftName = user.minecraftName,
                lastUpdated = lastUpdated
            )
        }
    }

    override suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        return query {
            UserRatingTable.insertAndGetId { statement ->
                statement[UserRatingTable.userCreatedReport] = reporter?.id
                statement[UserRatingTable.reportedUser] = reported.id
                statement[UserRatingTable.rating] = ratingValue
                statement[UserRatingTable.message] = message
                statement[UserRatingTable.time] = clock.now().toEpochMilliseconds()
                statement[UserRatingTable.ratingTypeIndex] = type.ordinal
            }.value
        }
    }

    override suspend fun deleteUserRating(rating: UserRating): Result<Unit> {
        return query {
            UserRatingTable.deleteWhere { UserRatingTable.id.eq(rating.id) }
        }.map { _ -> }
    }

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRating>> {
        val reportedUser = findUser(playerUUID).getOrElse { error -> return Result.failure(error) }
            ?: return Result.success(emptyList())
        return query {
            UserRatingTable
                .join(
                    otherTable = UserTable,
                    onColumn = UserRatingTable.userCreatedReport,
                    otherColumn = UserTable.id,
                    joinType = JoinType.LEFT,
                )
                .selectAll()
                .where { UserRatingTable.reportedUser.eq(reportedUser.id) }
                .map { row -> userRatingOf(row, reportedUser) }
        }
    }

    override suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int> {
        return query {
            val ratingsSum = UserRatingTable.rating.sum()

            UserRatingTable
                .join(
                    otherTable = UserTable,
                    onColumn = UserRatingTable.reportedUser,
                    otherColumn = UserTable.id,
                    joinType = JoinType.INNER,
                )
                .select(ratingsSum)
                .where { UserTable.minecraftUUID.eq(playerUUID.toString()) }
                .firstOrNull()
                ?.get(ratingsSum)
                ?: 0
        }
    }

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUser>> {
        return query {
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
                .map { row ->
                    RatedUser(
                        user = User(
                            id = row[UserTable.id].value,
                            minecraftUUID = row[UserTable.minecraftUUID],
                            minecraftName = row[UserTable.minecraftName],
                            lastUpdated = Instant.fromEpochMilliseconds(row[lastUpdated] ?: 0L),
                        ),
                        ratingTotal = row[ratingsSum] ?: 0,
                        ratingCounts = row[ratingsCount]
                    )
                }
        }
    }

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> {
        val user = findUser(playerUUID).getOrElse { error -> return Result.failure(error) }
            ?: return Result.success(0L)
        return query {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport.eq(user.id).and {
                        UserRatingTable.time.greater(clock.now().minus(1.days).toEpochMilliseconds())
                    }
                }.count()
        }
    }

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> {
        val playerCreatedReport = findUser(playerUUID).getOrElse { error -> return Result.failure(error) }
            ?: return Result.success(0L)
        val ratedPlayer = findUser(ratedPlayerUUID).getOrElse { error -> return Result.failure(error) }
            ?: return Result.success(0L)
        return query {
            UserRatingTable.selectAll()
                .where {
                    UserRatingTable.userCreatedReport
                        .eq(playerCreatedReport.id)
                        .and { UserRatingTable.reportedUser.eq(ratedPlayer.id) }
                }.count()
        }
    }

    companion object {
        private val MAX_TIMEOUT = 5.seconds
    }
}
