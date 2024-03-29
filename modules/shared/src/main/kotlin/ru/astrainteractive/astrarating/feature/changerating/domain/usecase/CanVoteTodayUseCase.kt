package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.core.RatingPermission
import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteTodayRepository
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteTodayUseCase.Input
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * Checks whether [Input.playerModel] can vote today or not
 *
 * Each player has limited amount of votes per day
 */
interface CanVoteTodayUseCase : UseCase.Suspended<Input, Boolean> {
    class Input(val playerModel: PlayerModel)

    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class CanVoteTodayUseCaseImpl(
    private val maxRatingPerDay: Int,
    private val canVoteTodayRepository: CanVoteTodayRepository
) : CanVoteTodayUseCase {
    override suspend fun invoke(input: Input): Boolean {
        val playerModel = input.playerModel
        val maxVotesPerDay = playerModel.permissible?.maxPermissionSize(
            RatingPermission.MaxRatePerDay
        ) ?: maxRatingPerDay
        val todayVotedAmount = canVoteTodayRepository.countPlayerTotalDayRated(playerModel.name)
        return todayVotedAmount <= maxVotesPerDay
    }
}
