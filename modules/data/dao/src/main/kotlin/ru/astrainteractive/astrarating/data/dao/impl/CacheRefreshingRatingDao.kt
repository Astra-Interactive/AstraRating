package ru.astrainteractive.astrarating.data.dao.impl

import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import java.util.UUID

internal class CacheRefreshingRatingDao(
    private val ratingDao: RatingDao,
    private val ratingCachedDao: RatingCachedDao
) : RatingDao by ratingDao {
    private suspend fun reloadRatingOf(user: UserDTO) {
        val uuid = runCatching { UUID.fromString(user.minecraftUUID) }.getOrNull() ?: return
        ratingCachedDao.loadPlayerRating(uuid)
    }

    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        val result = ratingDao.insertUserRating(reporter, reported, message, type, ratingValue)
        if (result.isSuccess) reloadRatingOf(reported)
        return result
    }

    override suspend fun deleteUserRating(it: UserRatingDTO): Result<*> {
        val result = ratingDao.deleteUserRating(it)
        if (result.isSuccess) reloadRatingOf(it.reportedUser)
        return result
    }
}
