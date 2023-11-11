package ru.astrainteractive.astrarating.event.di

import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.EventManager

interface EventModule {
    val eventManager: EventManager

    class Default(rootModule: RootModule) : EventModule {
        private val dependencies by lazy {
            EventDependencies.Default(rootModule)
        }
        override val eventManager: EventManager by lazy {
            EventManager(dependencies)
        }
    }
}
