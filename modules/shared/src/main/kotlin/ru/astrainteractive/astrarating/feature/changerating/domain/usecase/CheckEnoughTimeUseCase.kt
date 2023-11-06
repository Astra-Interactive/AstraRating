package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.feature.changerating.data.PlatformBridge
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckEnoughTimeUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks whether [Input.playerModel] has been enough time on server
 */
interface CheckEnoughTimeUseCase : UseCase.Suspended<Input, Boolean> {
    @JvmInline
    value class Input(val playerModel: PlayerModel)

    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class CheckEnoughTimeUseCaseImpl(
    private val platformBridge: PlatformBridge
) : CheckEnoughTimeUseCase {
    override suspend fun invoke(input: Input): Boolean {
        return platformBridge.hasEnoughTime(input.playerModel)
    }
}
