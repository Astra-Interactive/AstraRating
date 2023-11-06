package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckPlayerExistsUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks if [Input.playerModel] exists
 */
interface CheckPlayerExistsUseCase : UseCase.Suspended<Input, Boolean> {
    @JvmInline
    value class Input(val playerModel: PlayerModel?)

    suspend operator fun invoke(playerModel: PlayerModel?) = invoke(Input(playerModel))
}

internal class CheckPlayerExistsUseCaseImpl : CheckPlayerExistsUseCase {
    override suspend fun invoke(input: Input): Boolean {
        return TODO() // ratedPlayer == null || ratedPlayer.firstPlayed == 0L
    }
}
