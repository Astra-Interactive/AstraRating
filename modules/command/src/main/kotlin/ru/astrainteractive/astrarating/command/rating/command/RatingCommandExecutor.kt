package ru.astrainteractive.astrarating.command.rating.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.KCommandDispatcher
import ru.astrainteractive.astrarating.command.rating.model.RatingCommand
import ru.astrainteractive.astrarating.command.rating.util.toRatingPlayer
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.gui.api.rating.change.model.AddRatingResult
import ru.astrainteractive.astrarating.gui.api.rating.change.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.api.router.api.GuiRouter
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class RatingCommandExecutor(
    private val addRatingUseCase: AddRatingUseCase,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val router: GuiRouter,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) {
    private val translation by translationKrate

    private fun messageOf(result: AddRatingResult, input: RatingCommand.ChangeRating): LocalizableComponent {
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

    private fun changeRating(input: RatingCommand.ChangeRating) = coroutineScope.launch(dispatchers.IO) {
        val result = addRatingUseCase(
            creator = input.executor.toRatingPlayer(),
            ratedPlayer = input.ratedPlayer.toRatingPlayer(),
            message = input.message,
            rating = input.value,
            type = RatingType.USER_RATING
        )
        input.executor.sendMessage(messageOf(result, input))
    }

    private fun reload(input: RatingCommand.Reload) {
        when (input.executor) {
            is KCommandDispatcher -> input.executor.dispatchCommand("aratingreload")

            else -> Unit
        }
    }

    fun execute(input: RatingCommand) {
        when (input) {
            is RatingCommand.ChangeRating -> {
                changeRating(input)
            }

            is RatingCommand.OpenRatingsGui -> {
                val route = GuiRouter.Route.AllRatings(input.executor)
                router.navigate(route)
            }

            is RatingCommand.OpenPlayerRatingGui -> {
                val route = GuiRouter.Route.PlayerRating(
                    inventoryOwner = input.player,
                    selectedPlayerName = input.selectedPlayerName,
                    selectedPlayerUUID = input.selectedPlayerUUID
                )
                router.navigate(route)
            }

            is RatingCommand.Reload -> {
                reload(input)
            }
        }
    }
}
