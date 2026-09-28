package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
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
    private val coreModule: CoreModule,
    guiModule: GuiModule,
    platformServer: PlatformServer,
    ratingChangeModule: RatingChangeModule,
) {
    private val nodes = listOf(
        ReloadLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler,
            lifecyclePlugin = lifecyclePlugin,
            translationKrate = coreModule.translationKrate
        ).create(),
        RatingLiteralArgumentBuilder(
            commandExceptionHandler = coreModule.commandExceptionHandler,
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
