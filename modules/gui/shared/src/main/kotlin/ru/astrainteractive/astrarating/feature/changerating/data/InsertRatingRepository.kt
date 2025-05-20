package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO
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
    private val dbApi: RatingDBApi,
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
