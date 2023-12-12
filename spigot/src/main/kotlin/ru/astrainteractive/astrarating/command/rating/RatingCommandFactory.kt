package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory

@Suppress("LongParameterList")
class RatingCommandFactory(
    private val plugin: JavaPlugin,
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: PluginTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    private val translationContext: BukkitTranslationContext,
    private val guiRouter: GuiRouter
) : Factory<RatingCommand> {

    private inner class RatingCommandImpl :
        RatingCommand,
        Command<RatingCommand.Result, RatingCommand.Result> by DefaultCommandFactory.create(
            alias = "arating",
            commandExecutor = RatingCommandExecutor(
                addRatingUseCase = addRatingUseCase,
                translation = translation,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers,
                translationContext = translationContext,
                router = guiRouter
            ),
            commandParser = RatingCommandParser(),
            resultHandler = { commandSender, result ->
                when (result) {
                    RatingCommand.Result.NotPlayer -> with(translationContext) {
                        commandSender.sendMessage(translation.onlyPlayerCommand)
                    }

                    RatingCommand.Result.WrongUsage -> with(translationContext) {
                        commandSender.sendMessage(translation.wrongUsage)
                    }

                    is RatingCommand.Result.OpenPlayerRatingGui,
                    is RatingCommand.Result.Reload,
                    is RatingCommand.Result.OpenRatingsGui,
                    is RatingCommand.Result.ChangeRating -> Unit
                }
            },
            mapper = {
                when (it) {
                    is RatingCommand.Result.OpenRatingsGui -> {
                        RatingCommand.Result.OpenRatingsGui(it.executor)
                    }

                    is RatingCommand.Result.Reload -> {
                        RatingCommand.Result.Reload(it.executor)
                    }

                    is RatingCommand.Result.ChangeRating -> {
                        RatingCommand.Result.ChangeRating(
                            value = it.value,
                            message = it.message,
                            executor = it.executor,
                            ratedPlayer = it.ratedPlayer
                        )
                    }

                    RatingCommand.Result.WrongUsage -> null
                    RatingCommand.Result.NotPlayer -> null
                    is RatingCommand.Result.OpenPlayerRatingGui -> {
                        RatingCommand.Result.OpenPlayerRatingGui(
                            it.player,
                            it.selectedPlayerName
                        )
                    }
                }
            }
        )

    override fun create(): RatingCommand {
        return RatingCommandImpl().also {
            it.register(plugin)
        }
    }
}
