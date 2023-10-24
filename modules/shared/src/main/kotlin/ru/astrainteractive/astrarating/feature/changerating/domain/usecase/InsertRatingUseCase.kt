package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.feature.changerating.data.InsertRatingRepository
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertRatingUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Inserts new player rating and both players into database
 *
 * Each player has limited amount of votes per day
 */
interface InsertRatingUseCase : UseCase.Parametrized<Input, Unit> {
    class Input(
        val ratingCreator: PlayerModel,
        val ratedPlayer: PlayerModel,
        val message: String,
        val type: RatingType,
        val ratingValue: Int
    )
}

internal class InsertRatingUseCaseImpl(
    private val insertUserUseCase: InsertUserUseCase,
    private val insertRatingRepository: InsertRatingRepository
) : InsertRatingUseCase {
    override suspend fun invoke(input: Input) {
        val ratedPlayerDTO = insertUserUseCase.invoke(input.ratedPlayer)
        val ratingCreatorDTO = insertUserUseCase.invoke(input.ratingCreator)

        insertRatingRepository.insertUserRating(
            reporter = ratingCreatorDTO.playerDTO,
            reported = ratedPlayerDTO.playerDTO,
            message = input.message,
            type = input.type,
            ratingValue = input.ratingValue
        )
    }
}
