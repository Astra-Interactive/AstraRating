package ru.astrainteractive.astrarating.event.kill.usecase

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.util.isUserRenamed
import ru.astrainteractive.astrarating.event.kill.model.LowerKillerRatingResult
import ru.astrainteractive.astrarating.event.kill.util.changesRating
import ru.astrainteractive.astrarating.event.kill.util.isKillPenalized
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

internal class LowerKillerRatingUseCase(
    private val ratingRepository: RatingRepository,
    configKrate: CachedKrate<AstraRatingConfig>
) {
    private val config by configKrate

    private suspend fun upsertUser(newUser: NewUser): Result<User> {
        val storedUser = ratingRepository.findUser(newUser.minecraftUUID)
            .getOrElse { error -> return Result.failure(error) }
            ?: return ratingRepository.insertUser(newUser)
        if (!isUserRenamed(storedUser, newUser)) return Result.success(storedUser)
        val renamedUser = storedUser.copy(minecraftName = newUser.minecraftName)
        return ratingRepository.updateUser(renamedUser).map { _ -> renamedUser }
    }

    suspend operator fun invoke(killer: NewUser, killedPlayerUUID: UUID, reason: String): LowerKillerRatingResult {
        val killEvent = config.events.killPlayer
        if (!killEvent.changesRating) return LowerKillerRatingResult.Disabled
        val killedPlayerRating = ratingRepository.fetchUserTotalRating(killedPlayerUUID)
            .getOrElse { return LowerKillerRatingResult.StorageFailed }
        if (!killEvent.isKillPenalized(killedPlayerRating)) return LowerKillerRatingResult.KilledPlayerNotRated
        val storedKiller = upsertUser(killer)
            .getOrElse { return LowerKillerRatingResult.StorageFailed }
        return ratingRepository.insertUserRating(
            reporter = null,
            reported = storedKiller,
            message = reason,
            type = RatingType.PLAYER_KILL,
            ratingValue = killEvent.changeBy
        ).fold(
            onSuccess = { _ -> LowerKillerRatingResult.Lowered },
            onFailure = { _ -> LowerKillerRatingResult.StorageFailed }
        )
    }
}
