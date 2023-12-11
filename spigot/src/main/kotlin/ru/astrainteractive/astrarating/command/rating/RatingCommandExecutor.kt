package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.CommandExecutor
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.PluginTranslation

class RatingCommandExecutor(
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: PluginTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext,
    private val router: GuiRouter
) : CommandExecutor<RatingCommand.Input>,
    BukkitTranslationContext by translationContext {

    private fun OfflinePlayer.toPlayerModel(): PlayerModel? {
        return PlayerModel(
            uuid = this.uniqueId,
            name = name ?: return null,
            permissible = null
        )
    }

    private fun Player.toPlayerModel(): PlayerModel {
        return PlayerModel(
            uuid = this.uniqueId,
            name = name,
            permissible = this.toPermissible()
        )
    }

    private fun changeRating(input: RatingCommand.Input.ChangeRating) = coroutineScope.launch(dispatchers.IO) {
        val useCaseInput = AddRatingUseCase.Input(
            creator = input.executor.toPlayerModel(),
            ratedPlayerModel = input.rated.toPlayerModel(),
            message = input.message,
            rating = input.value,
            type = RatingType.USER_RATING
        )
        val result = runCatching { addRatingUseCase.invoke(useCaseInput) }
        result.onFailure {
            input.executor.sendMessage(translation.unknownError)
            it.printStackTrace()
        }
        result.onSuccess {
            when (it) {
                AddRatingUseCase.Output.AlreadyMaxDayVotes -> {
                    input.executor.sendMessage(translation.alreadyMaxDayVotes)
                }

                AddRatingUseCase.Output.AlreadyMaxVotesOnPlayer -> {
                    input.executor.sendMessage(translation.alreadyMaxPlayerVotes)
                }

                AddRatingUseCase.Output.MessageNotCorrect -> {
                    input.executor.sendMessage(translation.wrongMessageLen)
                }

                AddRatingUseCase.Output.NoPermission -> {
                    input.executor.sendMessage(translation.noPermission)
                }

                AddRatingUseCase.Output.NotEnoughOnServer -> {
                    input.executor.sendMessage(translation.notEnoughOnServer)
                }

                AddRatingUseCase.Output.PlayerNotExists -> {
                    input.executor.sendMessage(translation.playerNotExists)
                }

                AddRatingUseCase.Output.SamePlayer -> {
                    input.executor.sendMessage(translation.cantRateSelf)
                }

                AddRatingUseCase.Output.Success -> {
                    if (input.value > 0) {
                        input.executor.sendMessage(translation.likedUser(input.rated.name ?: "-"))
                    } else {
                        input.executor.sendMessage(translation.dislikedUser(input.rated.name ?: "-"))
                    }
                }
            }
        }
    }

    private fun reload(input: RatingCommand.Input.Reload) {
        when (input.executor) {
            is Player -> input.executor.performCommand("aratingreload")
            is ConsoleCommandSender -> {
                Bukkit.getServer().dispatchCommand(input.executor, "aratingreload")
            }

            else -> Unit
        }
    }

    override fun execute(input: RatingCommand.Input) {
        when (input) {
            is RatingCommand.Input.ChangeRating -> {
                changeRating(input)
            }

            is RatingCommand.Input.OpenRatingGui -> {
                val route = GuiRouter.Route.AllRatings(input.player)
                router.navigate(route)
            }

            is RatingCommand.Input.OpenPlayerRatingGui -> {
                val route = GuiRouter.Route.PlayerRating(input.player, input.selectedPlayerName)
                router.navigate(route)
            }

            is RatingCommand.Input.Reload -> {
                reload(input)
            }
        }
    }
}
