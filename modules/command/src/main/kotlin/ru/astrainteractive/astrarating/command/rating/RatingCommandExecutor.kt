package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.KCommandDispatcher
import ru.astrainteractive.astralibs.server.permission.KPermissible
import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.model.PlayerModel
import ru.astrainteractive.astrarating.feature.gui.router.GuiRouter
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class RatingCommandExecutor(
    private val addRatingUseCase: AddRatingUseCase,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val router: GuiRouter,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    private fun KPlayer.toPlayerModel(): PlayerModel? {
        return PlayerModel(
            uuid = this.uuid,
            name = name ?: return null,
            permissible = tryCast<KPermissible>(),
            hasPlayedBefore = hasPlayedBefore()
        )
    }

    private fun OnlineKPlayer.toPlayerModel(): PlayerModel {
        return PlayerModel(
            uuid = this.uuid,
            name = name,
            permissible = this,
            hasPlayedBefore = hasPlayedBefore()
        )
    }

    private fun changeRating(input: RatingCommand.Result.ChangeRating) = coroutineScope.launch(dispatchers.IO) {
        val useCaseInput = AddRatingUseCase.Input(
            creator = input.executor.toPlayerModel(),
            ratedPlayerModel = input.ratedPlayer.toPlayerModel(),
            message = input.message,
            rating = input.value,
            type = RatingType.USER_RATING
        )
        val result = runCatching { addRatingUseCase.invoke(useCaseInput) }
        result.onFailure {
            input.executor.sendMessage(translation.general.unknownError.component)
            it.printStackTrace()
        }
        result.onSuccess {
            when (it) {
                AddRatingUseCase.Output.AlreadyMaxDayVotes -> {
                    input.executor.sendMessage(translation.messages.alreadyMaxDayVotes.component)
                }

                AddRatingUseCase.Output.AlreadyMaxVotesOnPlayer -> {
                    input.executor.sendMessage(translation.messages.alreadyMaxPlayerVoted.component)
                }

                AddRatingUseCase.Output.MessageNotCorrect -> {
                    input.executor.sendMessage(translation.messages.wrongMessageLen.component)
                }

                AddRatingUseCase.Output.NoPermission -> {
                    input.executor.sendMessage(translation.general.noPermission.component)
                }

                AddRatingUseCase.Output.NotEnoughOnServer -> {
                    input.executor.sendMessage(translation.messages.notEnoughOnServer.component)
                }

                AddRatingUseCase.Output.PlayerNotExists -> {
                    input.executor.sendMessage(translation.general.playerNotExist.component)
                }

                AddRatingUseCase.Output.SamePlayer -> {
                    input.executor.sendMessage(translation.messages.cantRateSelf.component)
                }

                AddRatingUseCase.Output.Success -> {
                    if (input.value > 0) {
                        input.executor.sendMessage(
                            translation.messages.likedUser(input.ratedPlayer.name ?: "-").component
                        )
                    } else {
                        input.executor.sendMessage(
                            translation.messages.dislikedUser(input.ratedPlayer.name ?: "-").component
                        )
                    }
                }
            }
        }
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
                    executor = input.player,
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
