package ru.astrainteractive.astrarating.feature.playerrating.domain

import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase.Input
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase.Output
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

interface SortRatingUseCase : UseCase.Suspended<Input, Output> {
    class Input(val ratings: List<UserRatingDTO>, val sort: UserRatingsSort)
    class Output(val ratings: List<UserRatingDTO>)
}

class SortRatingUseCaseImpl : SortRatingUseCase {
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
