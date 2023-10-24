package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.plugin.RatingPermission
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks whether [Input.playerModel] has permission to vote or not
 */
interface CanVoteUseCase : UseCase.Parametrized<Input, Boolean> {
    @JvmInline
    value class Input(val playerModel: PlayerModel)

    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class CanVoteUseCaseImpl(
    private val permissionManager: PermissionManager
) : CanVoteUseCase {
    override suspend fun invoke(input: Input): Boolean {
        return permissionManager.hasPermission(input.playerModel.uuid, RatingPermission.Vote)
    }
}
