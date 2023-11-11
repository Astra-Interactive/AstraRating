package ru.astrainteractive.astrarating.command.di

import CommandManager
import ru.astrainteractive.astrarating.di.RootModule

interface CommandsModule {
    val commandManager: CommandManager

    class Default(rootModule: RootModule) : CommandsModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(rootModule)
            CommandManager(dependencies)
        }
    }
}
