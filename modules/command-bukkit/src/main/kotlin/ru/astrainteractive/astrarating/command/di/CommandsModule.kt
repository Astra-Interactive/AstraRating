package ru.astrainteractive.astrarating.command.di

import CommandManager
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule

interface CommandsModule {
    val lifecycle: Lifecycle

    class Default(
        sharedModule: SharedModule,
        bukkitModule: BukkitModule,
        coreModule: CoreModule,
        guiModule: GuiModule
    ) : CommandsModule {
        private val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                sharedModule = sharedModule,
                bukkitModule = bukkitModule,
                coreModule = coreModule,
                guiModule = guiModule
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
