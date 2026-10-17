package ru.astrainteractive.astrarating.gui.api.rating.change.usecase

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.util.upsertUser
import ru.astrainteractive.astrarating.gui.api.rating.change.model.AddRatingResult
import ru.astrainteractive.astrarating.gui.api.rating.change.model.RatingPlayer
import ru.astrainteractive.astrarating.gui.api.rating.change.model.isRatingMessageLengthAllowed
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

class AddRatingUseCase(
    private val ratingRepository: RatingRepository,
    configKrate: CachedKrate<AstraRatingConfig>
) {
    private val config by configKrate

    private suspend fun findVoteLimitReached(creator: RatingPlayer, ratedPlayer: RatingPlayer): AddRatingResult? {
        val todayVotes = ratingRepository.countPlayerTotalDayRated(creator.uuid)
            .getOrElse { return AddRatingResult.StorageFailed }
        if (!creator.hasVotesLeftToday(todayVotes, config)) return AddRatingResult.AlreadyMaxDayVotes
        val votesOnPlayer = ratingRepository.countPlayerOnPlayerDayRated(creator.uuid, ratedPlayer.uuid)
            .getOrElse { return AddRatingResult.StorageFailed }
        if (!creator.hasVotesLeftOnPlayerToday(votesOnPlayer, config)) {
            return AddRatingResult.AlreadyMaxVotesOnPlayer
        }
        return null
    }

    private suspend fun insertRating(
        creator: RatingPlayer,
        ratedPlayer: RatingPlayer,
        message: String,
        rating: Int,
        type: RatingType
    ): AddRatingResult {
        val reported = ratingRepository.upsertUser(ratedPlayer.toNewUser())
            .getOrElse { return AddRatingResult.StorageFailed }
        val reporter = ratingRepository.upsertUser(creator.toNewUser())
            .getOrElse { return AddRatingResult.StorageFailed }
        return ratingRepository.insertUserRating(
            reporter = reporter,
            reported = reported,
            message = message,
            type = type,
            ratingValue = rating
        ).fold(
            onSuccess = { _ -> AddRatingResult.Success },
            onFailure = { _ -> AddRatingResult.StorageFailed }
        )
    }

    suspend operator fun invoke(
        creator: RatingPlayer,
        ratedPlayer: RatingPlayer?,
        message: String,
        rating: Int,
        type: RatingType
    ): AddRatingResult {
        ratedPlayer ?: return AddRatingResult.PlayerNotExists
        if (!creator.canVote()) return AddRatingResult.NoPermission
        if (!ratedPlayer.hasPlayedBefore) return AddRatingResult.PlayerNotExists
        if (ratedPlayer.isSamePlayer(creator)) return AddRatingResult.SamePlayer
        val voteLimitReached = findVoteLimitReached(creator, ratedPlayer)
        if (voteLimitReached != null) return voteLimitReached
        if (!isRatingMessageLengthAllowed(message, config)) return AddRatingResult.MessageNotCorrect
        return insertRating(creator, ratedPlayer, message, rating, type)
    }
}
