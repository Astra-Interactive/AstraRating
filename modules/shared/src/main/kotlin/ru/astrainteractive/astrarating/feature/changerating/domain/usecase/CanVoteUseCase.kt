package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.core.RatingPermission
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks whether [Input.playerModel] has permission to vote or not
 */
interface CanVoteUseCase : UseCase.Suspended<Input, Boolean> {
    @JvmInline
    value class Input(val playerModel: PlayerModel)

    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class CanVoteUseCaseImpl : CanVoteUseCase {
    override suspend fun invoke(input: Input): Boolean {
        return input.playerModel.permissible?.hasPermission(RatingPermission.Vote) ?: false
    }
}
