package ru.astrainteractive.astrarating.feature.rating.change.domain.check

import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.feature.rating.change.data.PlayerOnPlayerCounterRepository
import ru.astrainteractive.astrarating.feature.rating.change.data.PlayerTotalRatingRepository
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

internal class CheckValidatorImpl(
    private val playerOnPlayerCounterRepository: PlayerOnPlayerCounterRepository,
    private val playerTotalRatingRepository: PlayerTotalRatingRepository,
    configKrate: CachedKrate<AstraRatingConfig>
) : CheckValidator {
    private val config by configKrate

    private suspend fun checkCanVoteOnPlayer(check: Check.CanVoteOnPlayer): Boolean {
        val maxVotePerPlayer = check.creator.permissible
            ?.maxPermissionSize(AstraRatingPermission.SinglePlayerPerDay)
            ?: config.maxRatingPerPlayer
        val votedOnPlayerAmount = playerOnPlayerCounterRepository.countPlayerOnPlayerDayRated(
            creatorUUID = check.creator.uuid,
            ratedUUID = check.rated?.uuid ?: error("Rated player doesn't have a name!")
        )
        return votedOnPlayerAmount < maxVotePerPlayer
    }

    private suspend fun checkCanVoteToday(check: Check.CanVoteToday): Boolean {
        val maxVotesPerDay = check.playerModel.permissible
            ?.maxPermissionSize(AstraRatingPermission.MaxRatePerDay)
            ?: config.maxRatingPerDay
        val todayVotedAmount = playerTotalRatingRepository.countPlayerTotalDayRated(check.playerModel.uuid)
        return todayVotedAmount < maxVotesPerDay
    }

    private fun checkCanVote(check: Check.CanVote): Boolean {
        return check.playerModel.permissible?.hasPermission(AstraRatingPermission.Vote) ?: false
    }

    private fun checkEnoughTime(check: Check.EnoughTime): Boolean {
        return System.currentTimeMillis() - check.playerModel.firstPlayed > config.minTimeOnServer
    }

    private fun checkPlayerExists(check: Check.PlayerExists): Boolean {
        return check.playerModel.firstPlayed > 0L
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
