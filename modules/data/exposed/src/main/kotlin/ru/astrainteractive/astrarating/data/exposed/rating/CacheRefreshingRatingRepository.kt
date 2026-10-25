package ru.astrainteractive.astrarating.data.exposed.rating

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import java.util.UUID

internal class CacheRefreshingRatingRepository(
    private val ratingRepository: RatingRepository,
    private val ratingCache: RatingCache
) : RatingRepository by ratingRepository {
    private suspend fun reloadRatingOf(user: User) {
        val uuid = runCatching { UUID.fromString(user.minecraftUUID) }
            .propagateCancellationException()
            .getOrNull() ?: return
        ratingCache.loadPlayerRating(uuid)
    }

    override suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        val result = ratingRepository.insertUserRating(
            reporter = reporter,
            reported = reported,
            message = message,
            type = type,
            ratingValue = ratingValue
        )
        if (result.isSuccess) reloadRatingOf(reported)
        return result
    }

    override suspend fun deleteUserRating(rating: UserRating): Result<Unit> {
        val result = ratingRepository.deleteUserRating(rating)
        if (result.isSuccess) reloadRatingOf(rating.reportedUser)
        return result
    }
}
