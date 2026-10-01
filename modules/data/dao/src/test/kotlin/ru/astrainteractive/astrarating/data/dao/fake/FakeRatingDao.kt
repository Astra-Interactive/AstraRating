package ru.astrainteractive.astrarating.data.dao.fake

import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import java.util.UUID

internal class FakeRatingDao : RatingDao {
    private val totalsByPlayer = mutableMapOf<UUID, Int>()

    var isAvailable: Boolean = true

    private fun notUsed(): Nothing = error("The tests do not call this")

    private fun addRating(user: UserDTO, ratingValue: Int) {
        totalsByPlayer.merge(UUID.fromString(user.minecraftUUID), ratingValue, Int::plus)
    }

    fun setTotalRating(playerUUID: UUID, total: Int) {
        totalsByPlayer[playerUUID] = total
    }

    override suspend fun selectUser(playerUUID: UUID): Result<UserDTO> = notUsed()

    override suspend fun updateUser(user: UserDTO): Result<*> = notUsed()

    override suspend fun insertUser(user: UserModel): Result<Long> = notUsed()

    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        addRating(reported, ratingValue)
        return Result.success(1L)
    }

    override suspend fun deleteUserRating(it: UserRatingDTO): Result<*> {
        addRating(it.reportedUser, -it.rating)
        return Result.success(Unit)
    }

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRatingDTO>> = notUsed()

    override suspend fun fetchUserTotalRating(playerUUID: UUID): Result<Int> {
        if (!isAvailable) return Result.failure(IllegalStateException("The database is unavailable"))
        return Result.success(totalsByPlayer[playerUUID] ?: 0)
    }

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUserDTO>> = notUsed()

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> = notUsed()

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> =
        notUsed()
}
