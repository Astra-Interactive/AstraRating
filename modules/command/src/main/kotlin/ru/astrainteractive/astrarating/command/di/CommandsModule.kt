package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astrarating.command.exception.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.rating.RatingCommandExecutor
import ru.astrainteractive.astrarating.command.rating.RatingLiteralArgumentBuilder
import ru.astrainteractive.astrarating.command.reload.ReloadLiteralArgumentBuilder
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.gui.di.GuiModule
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule

@Suppress("LongParameterList")
class CommandsModule(
    private val commandRegistrarContext: CommandRegistrarContext,
    private val lifecyclePlugin: Lifecycle,
    private val multiplatformCommand: MultiplatformCommand,
    coreModule: CoreModule,
    guiModule: GuiModule,
    platformServer: PlatformServer,
    ratingChangeModule: RatingChangeModule,
) {
    private val nodes = listOf(
        ReloadLiteralArgumentBuilder(
            multiplatformCommand = multiplatformCommand,
            lifecyclePlugin = lifecyclePlugin,
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate
        ).create(),
        RatingLiteralArgumentBuilder(
            commandExceptionHandler = CommandExceptionHandler(
                translationKrate = coreModule.translationKrate,
                kyoriKrate = coreModule.kyoriKrate,
                multiplatformCommand = multiplatformCommand
            ),
            ratingCommandExecutor = RatingCommandExecutor(
                addRatingUseCase = ratingChangeModule.addRatingUseCase,
                translationKrate = coreModule.translationKrate,
                coroutineScope = coreModule.ioScope,
                dispatchers = coreModule.dispatchers,
                kyoriKrate = coreModule.kyoriKrate,
                router = guiModule.router
            ),
            multiplatformCommand = multiplatformCommand,
            platformServer = platformServer,
        ).create()
    )
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                nodes.forEach(commandRegistrarContext::registerWhenReady)
            }
        )
    }
}
