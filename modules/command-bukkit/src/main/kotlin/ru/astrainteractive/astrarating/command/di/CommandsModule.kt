package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astrarating.command.exception.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.rating.RatingCommandExecutor
import ru.astrainteractive.astrarating.command.rating.createRatingCommandNode
import ru.astrainteractive.astrarating.command.reload.createReloadCommandNode
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.core.gui.di.GuiBukkitModule
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule

@Suppress("LongParameterList")
class CommandsModule(
    ratingChangeModule: RatingChangeModule,
    bukkitModule: BukkitModule,
    coreModule: CoreModule,
    guiBukkitModule: GuiBukkitModule,
    private val commandRegistrarContext: CommandRegistrarContext,
    private val multiplatformCommand: MultiplatformCommand<*>,
    private val lifecyclePlugin: Lifecycle,
    platformServer: PlatformServer
) {
    private val nodes = listOf(
        createReloadCommandNode(
            multiplatformCommand = multiplatformCommand,
            lifecyclePlugin = lifecyclePlugin,
            translationKrate = coreModule.translationKrate,
            kyoriKrate = bukkitModule.kyoriKrate
        ),
        createRatingCommandNode(
            commandExceptionHandler = CommandExceptionHandler(
                translationKrate = coreModule.translationKrate,
                kyoriKrate = bukkitModule.kyoriKrate,
                multiplatformCommand = multiplatformCommand
            ),
            ratingCommandExecutor = RatingCommandExecutor(
                addRatingUseCase = ratingChangeModule.addRatingUseCase,
                translationKrate = coreModule.translationKrate,
                coroutineScope = coreModule.ioScope,
                dispatchers = coreModule.dispatchers,
                kyoriKrate = bukkitModule.kyoriKrate,
                router = guiBukkitModule.router
            ),
            multiplatformCommand = multiplatformCommand,
            platformServer = platformServer,
        )
    )
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                nodes.forEach(commandRegistrarContext::registerWhenReady)
            }
        )
    }
}
