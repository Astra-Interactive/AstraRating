package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.model.PluginTranslation

class RatingCommand(
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: PluginTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : Command, BukkitTranslationContext by translationContext {
    private val executor = RatingCommandExecutor(
        addRatingUseCase = addRatingUseCase,
        translation = translation,
        coroutineScope = coroutineScope,
        dispatchers = dispatchers,
        translationContext = translationContext
    )
    private val parser = RatingCommandParser("arating")

    override fun register(plugin: JavaPlugin) {
        Command.registerDefault(
            plugin = plugin,
            resultHandler = { commandSender, result ->
                when (result) {
                    RatingCommandParser.Result.NotPlayer -> {
                        commandSender.sendMessage(translation.onlyPlayerCommand)
                    }

                    RatingCommandParser.Result.WrongUsage -> {
                        commandSender.sendMessage(translation.wrongUsage)
                    }

                    is RatingCommandParser.Result.Reload,
                    is RatingCommandParser.Result.Rating,
                    is RatingCommandParser.Result.ChangeRating -> Unit
                }
            },
            commandParser = parser,
            commandExecutor = executor,
            transform = {
                when (it) {
                    is RatingCommandParser.Result.Rating -> {
                        RatingCommandExecutor.Input.OpenRatingGui(it.executor)
                    }

                    is RatingCommandParser.Result.Reload -> {
                        RatingCommandExecutor.Input.Reload(it.executor)
                    }

                    is RatingCommandParser.Result.ChangeRating -> {
                        RatingCommandExecutor.Input.ChangeRating(
                            value = it.value,
                            message = it.message,
                            executor = it.executor,
                            rated = it.ratedPlayer
                        )
                    }

                    RatingCommandParser.Result.WrongUsage -> null
                    RatingCommandParser.Result.NotPlayer -> null
                }
            }
        )
    }
}
