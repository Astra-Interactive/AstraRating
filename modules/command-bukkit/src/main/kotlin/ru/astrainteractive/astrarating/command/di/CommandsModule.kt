package ru.astrainteractive.astrarating.command.di

import CommandManager
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.feature.changerating.di.RatingChangeModule
import ru.astrainteractive.astrarating.gui.di.GuiModule

interface CommandsModule {
    val lifecycle: Lifecycle

    class Default(
        ratingChangeModule: RatingChangeModule,
        bukkitModule: BukkitModule,
        coreModule: CoreModule,
        guiModule: GuiModule
    ) : CommandsModule {
        private val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                bukkitModule = bukkitModule,
                coreModule = coreModule,
                guiModule = guiModule,
                ratingChangeModule = ratingChangeModule
            )
            CommandManager(dependencies)
        }
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    commandManager
                }
            )
        }
    }
}
