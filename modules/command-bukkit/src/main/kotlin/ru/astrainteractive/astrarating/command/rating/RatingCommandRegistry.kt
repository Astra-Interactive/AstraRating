package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.commandfactory.BukkitCommandFactory
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistry
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistryContext.Companion.toCommandRegistryContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
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
) {

    fun register() {
        val command = BukkitCommandFactory.create(
            alias = "arating",
            commandExecutor = RatingCommandExecutor(
                addRatingUseCase = addRatingUseCase,
                translation = translation,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers,
                kyoriComponentSerializer = kyoriComponentSerializer,
                router = guiRouter
            ),
            commandParser = RatingCommandParser(),
            commandSideEffect = { context, result ->
                when (result) {
                    RatingCommand.Result.NotPlayer -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.onlyPlayerCommand.let(::toComponent))
                    }

                    RatingCommand.Result.WrongUsage -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.wrongUsage.let(::toComponent))
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
        BukkitCommandRegistry.register(command, plugin.toCommandRegistryContext())
    }
}
