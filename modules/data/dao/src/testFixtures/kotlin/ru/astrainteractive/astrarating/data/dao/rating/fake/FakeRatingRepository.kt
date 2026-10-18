package ru.astrainteractive.astrarating.data.dao.rating.fake

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingStorageError
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import java.util.UUID
import kotlin.time.Instant

@Suppress("TooManyFunctions")
class FakeRatingRepository : RatingRepository {
    private val usersByPlayer = mutableMapOf<UUID, User>()
    private val totalsByPlayer = mutableMapOf<UUID, Int>()
    private val dayVotesByPlayer = mutableMapOf<UUID, Long>()
    private val dayVotesOnPlayer = mutableMapOf<Vote, Long>()
    private var ratedUsers = emptyList<RatedUser>()

    val insertedRatings = mutableListOf<InsertedRating>()

    val failingCalls = mutableSetOf<Call>()

    var isAvailable: Boolean = true

    private fun notUsed(): Nothing = error("The tests do not call this")

    private fun <T> whenAvailable(call: Call, value: () -> T): Result<T> {
        if (!isAvailable || call in failingCalls) return Result.failure(RatingStorageError.DatabaseUnavailable())
        return Result.success(value())
    }

    private fun addRating(user: User, ratingValue: Int) {
        totalsByPlayer.merge(UUID.fromString(user.minecraftUUID), ratingValue, Int::plus)
    }

    fun setTotalRating(playerUUID: UUID, total: Int) {
        totalsByPlayer[playerUUID] = total
    }

    fun setDayVotes(playerUUID: UUID, count: Long) {
        dayVotesByPlayer[playerUUID] = count
    }

    fun setDayVotesOnPlayer(playerUUID: UUID, ratedPlayerUUID: UUID, count: Long) {
        dayVotesOnPlayer[Vote(playerUUID, ratedPlayerUUID)] = count
    }

    fun setRatedUsers(users: List<RatedUser>) {
        ratedUsers = users
    }

    fun storeUser(user: User) {
        usersByPlayer[UUID.fromString(user.minecraftUUID)] = user
    }

    override suspend fun findUser(playerUUID: UUID): Result<User?> = whenAvailable(Call.FIND_USER) {
        usersByPlayer[playerUUID]
    }

    override suspend fun updateUser(user: User): Result<Unit> = whenAvailable(Call.UPDATE_USER) { storeUser(user) }

    override suspend fun insertUser(user: NewUser): Result<User> = whenAvailable(Call.INSERT_USER) {
        val insertedUser = User(
            id = usersByPlayer.size + 1L,
            minecraftUUID = user.minecraftUUID.toString(),
            minecraftName = user.minecraftName,
            lastUpdated = Instant.fromEpochMilliseconds(0L)
        )
        storeUser(insertedUser)
        insertedUser
    }

    override suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> = whenAvailable(Call.INSERT_RATING) {
        addRating(reported, ratingValue)
        insertedRatings += InsertedRating(reporter, reported, message, type, ratingValue)
        insertedRatings.size.toLong()
    }

    override suspend fun deleteUserRating(rating: UserRating): Result<Unit> = whenAvailable(Call.DELETE_RATING) {
        addRating(rating.reportedUser, -rating.rating)
    }

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRating>> = notUsed()

    override suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int> =
        whenAvailable(Call.FETCH_TOTAL_RATING) {
            totalsByPlayer[playerUUID] ?: 0
        }

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUser>> =
        whenAvailable(Call.FETCH_USERS_TOTAL_RATING) { ratedUsers }

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> =
        whenAvailable(Call.COUNT_DAY_VOTES) {
            dayVotesByPlayer[playerUUID] ?: 0L
        }

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> =
        whenAvailable(Call.COUNT_DAY_VOTES_ON_PLAYER) {
            dayVotesOnPlayer[Vote(playerUUID, ratedPlayerUUID)] ?: 0L
        }

    enum class Call {
        FIND_USER,
        UPDATE_USER,
        INSERT_USER,
        INSERT_RATING,
        DELETE_RATING,
        FETCH_TOTAL_RATING,
        FETCH_USERS_TOTAL_RATING,
        COUNT_DAY_VOTES,
        COUNT_DAY_VOTES_ON_PLAYER
    }

    private data class Vote(val playerUUID: UUID, val ratedPlayerUUID: UUID)

    data class InsertedRating(
        val reporter: User?,
        val reported: User,
        val message: String,
        val type: RatingType,
        val ratingValue: Int
    )
}
