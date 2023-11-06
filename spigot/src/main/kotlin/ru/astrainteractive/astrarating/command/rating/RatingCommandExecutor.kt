package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.CommandExecutor
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.PluginTranslation

class RatingCommandExecutor(
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: PluginTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : CommandExecutor<RatingCommandExecutor.Input>,
    BukkitTranslationContext by translationContext {

    sealed interface Input {
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val rated: OfflinePlayer
        ) : Input

        class Reload(val executor: CommandSender) : Input
        class OpenRatingGui(val executor: CommandSender) : Input
    }

    private fun OfflinePlayer.toPlayerModel(): PlayerModel? {
        return PlayerModel(
            uuid = this.uniqueId,
            name = name ?: return null
        )
    }

    private fun Player.toPlayerModel(): PlayerModel {
        return PlayerModel(
            uuid = this.uniqueId,
            name = name
        )
    }

    private fun changeRating(input: Input.ChangeRating) = coroutineScope.launch(dispatchers.IO) {
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
                        input.executor.sendMessage(translation.likedUser)
                    } else {
                        input.executor.sendMessage(translation.dislikedUser)
                    }
                }
            }
        }
    }

    private fun reload(input: Input.Reload) {
        when (input.executor) {
            is Player -> input.executor.performCommand("aratingreload")
            is ConsoleCommandSender -> {
                Bukkit.getServer().dispatchCommand(input.executor, "aratingreload")
            }

            else -> Unit
        }
    }

    override fun execute(input: Input) {
        when (input) {
            is Input.ChangeRating -> {
                changeRating(input)
            }

            is Input.OpenRatingGui -> {
                TODO()
            }

            is Input.Reload -> {
                reload(input)
            }
        }
    }
}
