package ru.astrainteractive.astrarating.feature.rating.change.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface InsertRatingRepository {
    suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>
}

internal class InsertRatingRepositoryImpl(
    private val dbApi: RatingDao,
    private val dispatchers: KotlinDispatchers
) : InsertRatingRepository {
    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
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
