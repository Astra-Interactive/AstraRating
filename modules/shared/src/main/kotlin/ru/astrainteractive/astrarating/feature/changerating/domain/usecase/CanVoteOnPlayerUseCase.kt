package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astralibs.permission.PermissionManager
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
interface CanVoteOnPlayerUseCase : UseCase.Parametrized<Input, Boolean> {
    class Input(val creator: PlayerModel, val rated: PlayerModel?)

    suspend operator fun invoke(
        creator: PlayerModel,
        rated: PlayerModel?
    ) = invoke(Input(creator, rated))
}

internal class CanVoteOnPlayerUseCaseImpl(
    private val permissionManager: PermissionManager,
    private val maxRatingPerPlayer: Int,
    private val canVoteOnPlayerRepository: CanVoteOnPlayerRepository
) : CanVoteOnPlayerUseCase {
    override suspend fun invoke(input: Input): Boolean {
        val creator = input.creator
        val rated = input.rated
        val maxVotePerPlayer = permissionManager.maxPermissionSize(
            creator.uuid,
            RatingPermission.SinglePlayerPerDay
        ) ?: maxRatingPerPlayer
        val votedOnPlayerAmount = canVoteOnPlayerRepository.countPlayerOnPlayerDayRated(
            creator.name,
            rated?.name ?: "NULL"
        )
        return votedOnPlayerAmount <= maxVotePerPlayer
    }
}
