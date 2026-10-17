package ru.astrainteractive.astrarating.feature.rating.change.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface InsertRatingRepository {
    suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>
}

internal class InsertRatingRepositoryImpl(
    private val dbApi: RatingRepository,
    private val dispatchers: KotlinDispatchers
) : InsertRatingRepository {
    override suspend fun insertUserRating(
        reporter: User?,
        reported: User,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        return withContext(dispatchers.IO) {
            dbApi.updateUser(reported)
            if (reporter != null) {
                dbApi.updateUser(reporter)
            }
            dbApi.insertUserRating(
                reporter = reporter,
                reported = reported,
                message = message,
                type = type,
                ratingValue = ratingValue
            )
        }
    }
}
