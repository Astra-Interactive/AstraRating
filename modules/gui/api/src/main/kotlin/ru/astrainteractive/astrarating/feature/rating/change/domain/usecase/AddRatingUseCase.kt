package ru.astrainteractive.astrarating.feature.rating.change.domain.usecase

import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.model.PlayerModel
import ru.astrainteractive.astrarating.feature.rating.change.data.InsertRatingRepository
import ru.astrainteractive.astrarating.feature.rating.change.domain.check.Check
import ru.astrainteractive.astrarating.feature.rating.change.domain.check.CheckValidator
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase.Input
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase.Output

/**
 * This UseCase will try to add rating from player to another player
 */
interface AddRatingUseCase {
    class Input(
        val creator: PlayerModel,
        val message: String,
        val ratedPlayerModel: PlayerModel?,
        val rating: Int,
        val type: RatingType
    )

    sealed interface Output {
        data object NoPermission : Output
        data object PlayerNotExists : Output
        data object SamePlayer : Output
        data object NotEnoughOnServer : Output
        data object AlreadyMaxDayVotes : Output
        data object AlreadyMaxVotesOnPlayer : Output
        data object MessageNotCorrect : Output
        data object Success : Output
    }

    suspend fun invoke(input: Input): Output
}

@Suppress("LongParameterList")
internal class AddRatingUseCaseImpl(
    private val checkValidator: CheckValidator,
    private val insertUserUseCase: InsertUserUseCase,
    private val insertRatingRepository: InsertRatingRepository
) : AddRatingUseCase {
    override suspend fun invoke(input: Input): Output {
        input.ratedPlayerModel ?: return Output.PlayerNotExists
        val checkMap = listOf(
            Check.CanVote(input.creator) to Output.NoPermission,
            Check.PlayerExists(input.ratedPlayerModel) to Output.PlayerNotExists,
            Check.NotSamePlayer(input.ratedPlayerModel, input.creator) to Output.SamePlayer,
            Check.EnoughTime(input.creator) to Output.NotEnoughOnServer,
            Check.CanVoteToday(input.creator) to Output.AlreadyMaxDayVotes,
            Check.CanVoteOnPlayer(input.creator, input.ratedPlayerModel) to Output.AlreadyMaxVotesOnPlayer,
            Check.MessageCorrect(input.message) to Output.MessageNotCorrect
        )
        val invalidCheck = checkMap.firstOrNull { (check, _) -> !checkValidator.isValid(check) }
        if (invalidCheck != null) return invalidCheck.second

        val ratedPlayerDTO = insertUserUseCase.invoke(input.ratedPlayerModel)
        val ratingCreatorDTO = insertUserUseCase.invoke(input.creator)

        insertRatingRepository.insertUserRating(
            reporter = ratingCreatorDTO.playerDTO,
            reported = ratedPlayerDTO.playerDTO,
            message = input.message,
            type = input.type,
            ratingValue = input.rating
        )

        return Output.Success
    }
}
