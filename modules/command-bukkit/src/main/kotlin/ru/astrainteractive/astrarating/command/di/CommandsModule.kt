package ru.astrainteractive.astrarating.command.di

import CommandManager
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
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
    private val commandManager: CommandManager by lazy {
        val dependencies = CommandsDependencies.Default(
            bukkitModule = bukkitModule,
            coreModule = coreModule,
            guiBukkitModule = guiBukkitModule,
            ratingChangeModule = ratingChangeModule
        )
        CommandManager(dependencies)
    }
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                commandManager
            }
        )
    }
}
