package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.ValidateMessageUseCase.Input
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Validates [Input.message]
 *
 * It should be in range of min/max range
 */
interface ValidateMessageUseCase : UseCase.Suspended<Input, Boolean> {
    @JvmInline
    value class Input(val message: String)

    suspend operator fun invoke(message: String) = invoke(Input(message))
}

internal class ValidateMessageUseCaseImpl(
    private val minMessageLength: Int,
    private val maxMessageLength: Int
) : ValidateMessageUseCase {
    override suspend fun invoke(input: Input): Boolean {
        val message = input.message
        return !(message.length < minMessageLength || message.length > maxMessageLength)
    }
}
