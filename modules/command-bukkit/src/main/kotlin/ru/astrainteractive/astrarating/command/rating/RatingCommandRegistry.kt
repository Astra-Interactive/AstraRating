package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrarating.core.gui.router.GuiRouter
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class RatingCommandRegistry(
    private val plugin: JavaPlugin,
    private val addRatingUseCase: AddRatingUseCase,
    private val translation: AstraRatingTranslation,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer,
    private val guiRouter: GuiRouter
) : Logger by JUtiltLogger("AstraRating-RatingCommandRegistry") {

    fun register() {
        plugin.setCommandExecutor(
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
