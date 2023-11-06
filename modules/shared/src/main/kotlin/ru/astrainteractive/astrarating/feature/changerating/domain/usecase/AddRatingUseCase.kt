package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase.Input
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase.Output
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * This UseCase will try to add rating from player to another player
 */
interface AddRatingUseCase : UseCase.Suspended<Input, Output> {
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
}

@Suppress("LongParameterList")
internal class AddRatingUseCaseImpl(
    private val canVoteOnPlayerUseCase: CanVoteOnPlayerUseCase,
    private val canVoteTodayUseCase: CanVoteTodayUseCase,
    private val canVoteUseCase: CanVoteUseCase,
    private val checkEnoughTimeUseCase: CheckEnoughTimeUseCase,
    private val checkPlayerExistsUseCase: CheckPlayerExistsUseCase,
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val insertRatingUseCase: InsertRatingUseCase
) : AddRatingUseCase {
    override suspend fun invoke(input: Input): Output {
        input.ratedPlayerModel ?: return Output.PlayerNotExists

        val canVote = canVoteUseCase.invoke(input.creator)
        if (!canVote) return Output.NoPermission

        val isPlayerExists = checkPlayerExistsUseCase.invoke(input.ratedPlayerModel)
        if (!isPlayerExists) return Output.PlayerNotExists

        val isSamePlayer = (input.ratedPlayerModel.uuid == input.creator.uuid)
        if (isSamePlayer) return Output.SamePlayer

        val isEnoughOnServer = checkEnoughTimeUseCase.invoke(input.creator)
        if (!isEnoughOnServer) return Output.NotEnoughOnServer

        val canVoteToday = canVoteTodayUseCase.invoke(input.creator)
        if (!canVoteToday) return Output.AlreadyMaxDayVotes

        val canVoteOnPlayer = canVoteOnPlayerUseCase.invoke(
            creator = input.creator,
            rated = input.ratedPlayerModel
        )
        if (!canVoteOnPlayer) return Output.AlreadyMaxVotesOnPlayer

        val isMessageCorrect = validateMessageUseCase.invoke(input.message)
        if (!isMessageCorrect) return Output.MessageNotCorrect

        InsertRatingUseCase.Input(
            ratingCreator = input.creator,
            ratedPlayer = input.ratedPlayerModel,
            message = input.message,
            type = input.type,
            ratingValue = input.rating
        ).let { input -> insertRatingUseCase.invoke(input) }

        return Output.Success
    }
}
