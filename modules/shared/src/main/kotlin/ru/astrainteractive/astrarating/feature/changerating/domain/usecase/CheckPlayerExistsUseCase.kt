package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.feature.changerating.data.PlatformBridge
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

internal class CheckPlayerExistsUseCaseImpl(
    private val platformBridge: PlatformBridge
) : CheckPlayerExistsUseCase {
    override suspend fun invoke(input: Input): Boolean {
        return platformBridge.isPlayerExists(input.playerModel)
    }
}
