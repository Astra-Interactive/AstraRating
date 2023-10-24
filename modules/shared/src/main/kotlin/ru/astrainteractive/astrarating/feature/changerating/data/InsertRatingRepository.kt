package ru.astrainteractive.astrarating.feature.changerating.data

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO

interface InsertRatingRepository {
    suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long>
}

internal class InsertRatingRepositoryImpl(private val dbApi: RatingDBApi) : InsertRatingRepository {
    override suspend fun insertUserRating(
        reporter: UserDTO?,
        reported: UserDTO,
        message: String,
        type: RatingType,
        ratingValue: Int
    ): Result<Long> {
        return dbApi.insertUserRating(
            reporter = reporter,
            reported = reported,
            message = message,
            type = type,
            ratingValue = ratingValue
        )
    }
}
