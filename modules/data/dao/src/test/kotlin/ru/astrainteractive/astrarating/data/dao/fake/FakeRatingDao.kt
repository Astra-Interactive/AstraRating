package ru.astrainteractive.astrarating.data.dao.fake

import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import java.util.UUID

internal class FakeRatingDao : RatingDao {
    private val ratingsByPlayer = mutableMapOf<UUID, List<UserRatingDTO>>()

    private fun notUsed(): Nothing = error("The cached rating does not call this")

    fun setRatings(playerUUID: UUID, ratings: List<UserRatingDTO>) {
        ratingsByPlayer[playerUUID] = ratings
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
    ): Result<Long> = notUsed()

    override suspend fun deleteUserRating(it: UserRatingDTO): Result<*> = notUsed()

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRatingDTO>> {
        val ratings = ratingsByPlayer[playerUUID]
            ?: return Result.failure(IllegalStateException("Could not find user with uuid $playerUUID"))
        return Result.success(ratings)
    }

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUserDTO>> = notUsed()

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> = notUsed()

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> =
        notUsed()
}
