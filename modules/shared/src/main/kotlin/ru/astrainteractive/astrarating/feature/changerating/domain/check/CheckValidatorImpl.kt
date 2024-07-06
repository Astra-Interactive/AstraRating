package ru.astrainteractive.astrarating.feature.changerating.domain.check

import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.RatingPermission
import ru.astrainteractive.astrarating.feature.changerating.data.PlatformBridge
import ru.astrainteractive.astrarating.feature.changerating.data.PlayerOnPlayerCounterRepository
import ru.astrainteractive.astrarating.feature.changerating.data.PlayerTotalRatingRepository

internal class CheckValidatorImpl(
    private val platformBridge: PlatformBridge,
    private val playerOnPlayerCounterRepository: PlayerOnPlayerCounterRepository,
    private val playerTotalRatingRepository: PlayerTotalRatingRepository,
    private val config: EmpireConfig
) : CheckValidator {
    private suspend fun checkCanVoteOnPlayer(check: Check.CanVoteOnPlayer): Boolean {
        val maxVotePerPlayer = check.creator.permissible
            ?.maxPermissionSize(RatingPermission.SinglePlayerPerDay)
            ?: config.maxRatingPerPlayer
        val votedOnPlayerAmount = playerOnPlayerCounterRepository.countPlayerOnPlayerDayRated(
            creatorName = check.creator.name,
            ratedName = check.rated?.name ?: error("Rated player doesn't have a name!")
        )
        return votedOnPlayerAmount <= maxVotePerPlayer
    }

    private suspend fun checkCanVoteToday(check: Check.CanVoteToday): Boolean {
        val maxVotesPerDay = check.playerModel.permissible
            ?.maxPermissionSize(RatingPermission.MaxRatePerDay)
            ?: config.maxRatingPerDay
        val todayVotedAmount = playerTotalRatingRepository.countPlayerTotalDayRated(check.playerModel.name)
        return todayVotedAmount <= maxVotesPerDay
    }

    private fun checkCanVote(check: Check.CanVote): Boolean {
        return check.playerModel.permissible?.hasPermission(RatingPermission.Vote) ?: false
    }

    private fun checkEnoughTime(check: Check.EnoughTime): Boolean {
        return platformBridge.hasEnoughTime(check.playerModel)
    }

    private fun checkPlayerExists(check: Check.PlayerExists): Boolean {
        return platformBridge.isPlayerExists(check.playerModel)
    }

    private fun checkMessageCorrect(check: Check.MessageCorrect): Boolean {
        val message = check.message
        return !(message.length < config.minMessageLength || message.length > config.maxMessageLength)
    }

    private fun checkNotSamePlayer(check: Check.NotSamePlayer): Boolean {
        return check.first.uuid != check.second.uuid
    }

    override suspend fun isValid(check: Check): Boolean {
        return when (check) {
            is Check.CanVote -> checkCanVote(check)
            is Check.CanVoteOnPlayer -> checkCanVoteOnPlayer(check)
            is Check.CanVoteToday -> checkCanVoteToday(check)
            is Check.EnoughTime -> checkEnoughTime(check)
            is Check.MessageCorrect -> checkMessageCorrect(check)
            is Check.PlayerExists -> checkPlayerExists(check)
            is Check.NotSamePlayer -> checkNotSamePlayer(check)
        }
    }
}
