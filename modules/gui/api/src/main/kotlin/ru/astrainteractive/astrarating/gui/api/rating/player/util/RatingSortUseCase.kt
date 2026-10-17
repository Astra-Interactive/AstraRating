package ru.astrainteractive.astrarating.gui.api.rating.player.util

import ru.astrainteractive.astrarating.core.util.sortedBy
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import ru.astrainteractive.astrarating.gui.api.rating.player.util.RatingSortUseCase.Input
import ru.astrainteractive.astrarating.gui.api.rating.player.util.RatingSortUseCase.Output

internal interface RatingSortUseCase {
    class Input(val ratings: List<UserRating>, val sort: UserRatingsSort)
    class Output(val ratings: List<UserRating>)

    suspend fun invoke(input: Input): Output
}

internal class RatingSortUseCaseImpl : RatingSortUseCase {
    override suspend fun invoke(input: Input): Output {
        val ratings = input.ratings
        return when (input.sort) {
            is UserRatingsSort.Date -> ratings.sortedBy(input.sort.isAsc) {
                it.time
            }

            is UserRatingsSort.Player -> ratings.sortedBy(input.sort.isAsc) {
                it.userCreatedReport?.minecraftName
            }

            is UserRatingsSort.Rating -> ratings.sortedBy(input.sort.isAsc) {
                it.rating
            }
        }.let(::Output)
    }
}
