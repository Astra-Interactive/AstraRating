package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteOnPlayerRepository
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteOnPlayerUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.plugin.RatingPermission
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks whether [Input.creator] can vote today on [Input.rated] or not
 *
 * Player has limited votes on each player
 */
interface CanVoteOnPlayerUseCase : UseCase.Suspended<Input, Boolean> {
    class Input(val creator: PlayerModel, val rated: PlayerModel?)

    suspend operator fun invoke(
        creator: PlayerModel,
        rated: PlayerModel?
    ) = invoke(Input(creator, rated))
}

internal class CanVoteOnPlayerUseCaseImpl(
    private val maxRatingPerPlayer: Int,
    private val canVoteOnPlayerRepository: CanVoteOnPlayerRepository
) : CanVoteOnPlayerUseCase {
    override suspend fun invoke(input: Input): Boolean {
        val creator = input.creator
        val rated = input.rated
        val maxVotePerPlayer = creator.permissible?.maxPermissionSize(
            RatingPermission.SinglePlayerPerDay
        ) ?: maxRatingPerPlayer
        val votedOnPlayerAmount = canVoteOnPlayerRepository.countPlayerOnPlayerDayRated(
            creator.name,
            rated?.name ?: error("Rated player doesn't have a name!")
        )
        return votedOnPlayerAmount <= maxVotePerPlayer
    }
}
