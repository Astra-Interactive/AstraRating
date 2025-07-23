package ru.astrainteractive.astrarating.feature.ratings.player.domain

import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.feature.ratings.player.domain.RatingSortUseCase.Input
import ru.astrainteractive.astrarating.feature.ratings.player.domain.RatingSortUseCase.Output

internal interface RatingSortUseCase {
    class Input(val ratings: List<UserRatingDTO>, val sort: UserRatingsSort)
    class Output(val ratings: List<UserRatingDTO>)

    suspend fun invoke(input: Input): Output
}

internal class RatingSortUseCaseImpl : RatingSortUseCase {
    override suspend fun invoke(input: Input): Output {
        val ratings = input.ratings
        return when (input.sort) {
            UserRatingsSort.DATE_ASC -> ratings.sortedBy {
                it.time
            }

            UserRatingsSort.DATE_DESC -> ratings.sortedByDescending {
                it.time
            }

            UserRatingsSort.PLAYER_ASC -> ratings.sortedBy {
                it.userCreatedReport?.minecraftName
            }

            UserRatingsSort.PLAYER_DESC -> ratings.sortedByDescending {
                it.userCreatedReport?.minecraftName
            }

            UserRatingsSort.RATING_ASC -> ratings.sortedBy {
                it.rating
            }

            UserRatingsSort.RATING_DESC -> ratings.sortedBy {
                it.rating
            }
        }.let(::Output)
    }
}
