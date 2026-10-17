package ru.astrainteractive.astrarating.data.dao.rating.fake

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import java.util.UUID

@Suppress("TooManyFunctions")
class FakeRatingRepository : RatingRepository {
    private val totalsByPlayer = mutableMapOf<UUID, Int>()

    var isAvailable: Boolean = true

    private fun notUsed(): Nothing = error("The tests do not call this")

    private fun addRating(user: User, ratingValue: Int) {
        totalsByPlayer.merge(UUID.fromString(user.minecraftUUID), ratingValue, Int::plus)
    }

    fun setTotalRating(playerUUID: UUID, total: Int) {
        totalsByPlayer[playerUUID] = total
    }

    override suspend fun selectUser(playerUUID: UUID): Result<User> = notUsed()

    override suspend fun updateUser(user: User): Result<*> = notUsed()

    override suspend fun insertUser(user: NewUser): Result<Long> = notUsed()

    override suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        addRating(reported, ratingValue)
        return Result.success(1L)
    }

    override suspend fun deleteUserRating(it: UserRating): Result<*> {
        addRating(it.reportedUser, -it.rating)
        return Result.success(Unit)
    }

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRating>> = notUsed()

    override suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int> {
        if (!isAvailable) return Result.failure(IllegalStateException("The database is unavailable"))
        return Result.success(totalsByPlayer[playerUUID] ?: 0)
    }

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUser>> = notUsed()

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> = notUsed()

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> =
        notUsed()
}
