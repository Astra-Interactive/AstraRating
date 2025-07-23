package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.command.rating.RatingCommandExecutor
import ru.astrainteractive.astrarating.command.rating.createRatingCommandNode
import ru.astrainteractive.astrarating.command.reload.createReloadCommandNode
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.core.gui.di.GuiBukkitModule
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule

class CommandsModule(
    ratingChangeModule: RatingChangeModule,
    bukkitModule: BukkitModule,
    coreModule: CoreModule,
    guiBukkitModule: GuiBukkitModule
) {
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                bukkitModule.commandRegistrarContext.registerWhenReady(
                    node = createReloadCommandNode(
                        lifecyclePlugin = bukkitModule.plugin,
                        translationKrate = coreModule.translationKrate,
                        kyoriKrate = bukkitModule.kyoriKrate
                    )
                )
                bukkitModule.commandRegistrarContext.registerWhenReady(
                    node = createRatingCommandNode(
                        kyoriKrate = bukkitModule.kyoriKrate,
                        ratingCommandExecutor = RatingCommandExecutor(
                            addRatingUseCase = ratingChangeModule.addRatingUseCase,
                            translationKrate = coreModule.translationKrate,
                            coroutineScope = coreModule.ioScope,
                            dispatchers = coreModule.dispatchers,
                            kyoriKrate = bukkitModule.kyoriKrate,
                            router = guiBukkitModule.router
                        )
                    )
                )
            }
        )
    }
}
