package ru.astrainteractive.astrarating.gui.api.rating.change.usecase

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.util.isUserRenamed
import ru.astrainteractive.astrarating.data.dao.rating.util.rename
import ru.astrainteractive.astrarating.gui.api.rating.change.model.AddRatingResult
import ru.astrainteractive.astrarating.gui.api.rating.change.model.RatingPlayer
import ru.astrainteractive.astrarating.gui.api.rating.change.util.isRatingMessageLengthAllowed
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
        if (!creator.hasVotesLeftOnPlayerToday(votesOnPlayer, config)) return AddRatingResult.AlreadyMaxVotesOnPlayer
        return null
    }

    private suspend fun upsertUser(newUser: NewUser): Result<User> {
        val storedUser = ratingRepository.findUser(newUser.minecraftUUID)
            .getOrElse { error -> return Result.failure(error) }
            ?: return ratingRepository.insertUser(newUser)
        if (!isUserRenamed(storedUser, newUser)) return Result.success(storedUser)
        val renamedUser = storedUser.rename(newUser)
        return ratingRepository.updateUser(renamedUser).map { _ -> renamedUser }
    }

    private suspend fun insertRating(
        creator: RatingPlayer,
        ratedPlayer: RatingPlayer,
        message: String,
        rating: Int,
        type: RatingType
    ): AddRatingResult {
        val reported = upsertUser(ratedPlayer.toNewUser())
            .getOrElse { return AddRatingResult.StorageFailed }
        val reporter = upsertUser(creator.toNewUser())
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
        if (!config.isRatingMessageLengthAllowed(message)) return AddRatingResult.MessageNotCorrect
        return insertRating(
            creator = creator,
            ratedPlayer = ratedPlayer,
            message = message,
            rating = rating,
            type = type
        )
    }
}
