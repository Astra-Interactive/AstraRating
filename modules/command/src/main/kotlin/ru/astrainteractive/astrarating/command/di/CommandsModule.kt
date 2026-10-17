package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astrarating.command.error.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.rating.command.RatingCommandExecutor
import ru.astrainteractive.astrarating.command.rating.command.RatingLiteralArgumentBuilder
import ru.astrainteractive.astrarating.command.reload.command.ReloadLiteralArgumentBuilder
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.gui.api.di.GuiModule
import ru.astrainteractive.astrarating.gui.api.rating.change.di.RatingChangeModule

@Suppress("LongParameterList")
class CommandsModule(
    private val commandRegistrarContext: CommandRegistrarContext,
    private val lifecyclePlugin: Lifecycle,
    private val coreModule: CoreModule,
    guiModule: GuiModule,
    platformServer: PlatformServer,
    ratingChangeModule: RatingChangeModule,
) {
    private val commandExceptionHandler = CommandExceptionHandler(
        multiplatformCommand = coreModule.multiplatformCommand,
        translationKrate = coreModule.translationKrate
    )

    private val nodes = listOf(
        ReloadLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler,
            lifecyclePlugin = lifecyclePlugin,
            translationKrate = coreModule.translationKrate
        ).create(),
        RatingLiteralArgumentBuilder(
            commandExceptionHandler = commandExceptionHandler,
            ratingCommandExecutor = RatingCommandExecutor(
                addRatingUseCase = ratingChangeModule.addRatingUseCase,
                translationKrate = coreModule.translationKrate,
                coroutineScope = coreModule.ioScope,
                dispatchers = coreModule.dispatchers,
                router = guiModule.router
            ),
            multiplatformCommand = coreModule.multiplatformCommand,
            platformServer = platformServer,
        ).create()
    )
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                commandRegistrarContext.registerWhenReady(nodes, coreModule.unconfinedScope)
            }
        )
    }
}
