package ru.astrainteractive.astrarating.data.dao.fake

import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import java.util.UUID

/** Answers only the total ratings; the cached rating reads nothing else. */
internal class FakeRatingDao(
    private val usersTotalRating: Result<List<RatedUserDTO>>
) : RatingDao {
    private fun notUsed(): Nothing = error("The cached rating does not call this")

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

    override suspend fun fetchUserRatings(playerUUID: UUID): Result<List<UserRatingDTO>> = notUsed()

    override suspend fun fetchUsersTotalRating(): Result<List<RatedUserDTO>> = usersTotalRating

    override suspend fun countPlayerTotalDayRated(playerUUID: UUID): Result<Long> = notUsed()

    override suspend fun countPlayerOnPlayerDayRated(playerUUID: UUID, ratedPlayerUUID: UUID): Result<Long> =
        notUsed()
}
