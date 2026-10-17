package ru.astrainteractive.astrarating.command.rating.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.KCommandDispatcher
import ru.astrainteractive.astralibs.server.permission.KPermissible
import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astrarating.command.rating.model.RatingCommand
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.gui.api.rating.change.model.AddRatingResult
import ru.astrainteractive.astrarating.gui.api.rating.change.model.RatingPlayer
import ru.astrainteractive.astrarating.gui.api.rating.change.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.api.router.api.GuiRouter
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class RatingCommandExecutor(
    private val addRatingUseCase: AddRatingUseCase,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val router: GuiRouter,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) {
    private val translation by translationKrate

    private fun KPlayer.toRatingPlayer(): RatingPlayer? {
        return RatingPlayer(
            uuid = this.uuid,
            name = name ?: return null,
            permissible = tryCast<KPermissible>(),
            hasPlayedBefore = hasPlayedBefore()
        )
    }

    private fun OnlineKPlayer.toRatingPlayer(): RatingPlayer {
        return RatingPlayer(
            uuid = this.uuid,
            name = name,
            permissible = this,
            hasPlayedBefore = hasPlayedBefore()
        )
    }

    private fun messageOf(result: AddRatingResult, input: RatingCommand.Result.ChangeRating): LocalizableComponent {
        val ratedPlayerName = input.ratedPlayer.name ?: "-"
        return when (result) {
            AddRatingResult.AlreadyMaxDayVotes -> translation.ratingChange.dailyLimit
            AddRatingResult.AlreadyMaxVotesOnPlayer -> translation.ratingChange.playerLimit
            AddRatingResult.MessageNotCorrect -> translation.ratingChange.wrongMessageLength
            AddRatingResult.NoPermission -> translation.commandError.noPermission
            AddRatingResult.PlayerNotExists -> translation.commandError.playerNotFound
            AddRatingResult.SamePlayer -> translation.ratingChange.cannotRateSelf
            AddRatingResult.StorageFailed -> translation.commandError.unknownError
            AddRatingResult.Success -> if (input.value > 0) {
                translation.ratingChange.liked(ratedPlayerName)
            } else {
                translation.ratingChange.disliked(ratedPlayerName)
            }
        }
    }

    private fun changeRating(input: RatingCommand.Result.ChangeRating) = coroutineScope.launch(dispatchers.IO) {
        val result = addRatingUseCase(
            creator = input.executor.toRatingPlayer(),
            ratedPlayer = input.ratedPlayer.toRatingPlayer(),
            message = input.message,
            rating = input.value,
            type = RatingType.USER_RATING
        )
        input.executor.sendMessage(messageOf(result, input))
    }

    private fun reload(input: RatingCommand.Result.Reload) {
        when (input.executor) {
            is KCommandDispatcher -> input.executor.dispatchCommand("aratingreload")

            else -> Unit
        }
    }

    fun execute(input: RatingCommand.Result) {
        when (input) {
            is RatingCommand.Result.ChangeRating -> {
                changeRating(input)
            }

            is RatingCommand.Result.OpenRatingsGui -> {
                val route = GuiRouter.Route.AllRatings(input.executor)
                router.navigate(route)
            }

            is RatingCommand.Result.OpenPlayerRatingGui -> {
                val route = GuiRouter.Route.PlayerRating(
                    inventoryOwner = input.player,
                    selectedPlayerName = input.selectedPlayerName,
                    selectedPlayerUUID = input.selectedPlayerUUID
                )
                router.navigate(route)
            }

            is RatingCommand.Result.Reload -> {
                reload(input)
            }
        }
    }
}
