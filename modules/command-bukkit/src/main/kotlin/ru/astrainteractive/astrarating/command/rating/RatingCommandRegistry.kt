package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.util.PluginExt.registerCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class RatingCommandRegistry(
    private val plugin: JavaPlugin,
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: PluginTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer,
    private val guiRouter: GuiRouter
) : Logger by JUtiltLogger("RatingCommandRegistry") {

    fun register() {
        plugin.registerCommand(
            alias = "arating",
            commandParser = RatingCommandParser(),
            commandExecutor = RatingCommandExecutor(
                addRatingUseCase = addRatingUseCase,
                translation = translation,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers,
                kyoriComponentSerializer = kyoriComponentSerializer,
                router = guiRouter
            ),
            errorHandler = ErrorHandler { commandContext, throwable ->
                when (throwable) {
                    is RatingCommand.Error.NotPlayer -> with(kyoriComponentSerializer) {
                        commandContext.sender.sendMessage(translation.onlyPlayerCommand.component)
                    }
                    is RatingCommand.Error.WrongUsage -> with(kyoriComponentSerializer) {
                        commandContext.sender.sendMessage(translation.wrongUsage.component)
                    }
                    else -> warn { "#register unhandled exception ${throwable::class}" }
                }
            }
        )
    }
}
