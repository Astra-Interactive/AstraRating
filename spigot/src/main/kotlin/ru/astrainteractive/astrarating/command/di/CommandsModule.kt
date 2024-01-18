package ru.astrainteractive.astrarating.command.di

import CommandManager
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.di.RootModule

interface CommandsModule {
    val lifecycle: Lifecycle
    val commandManager: CommandManager

    class Default(rootModule: RootModule) : CommandsModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(rootModule)
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
