package ru.astrainteractive.astrarating.event.di

import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.EventManager

interface EventModule {
    val lifecycle: Lifecycle
    val eventManager: EventManager

    class Default(rootModule: RootModule) : EventModule {
        private val dependencies by lazy {
            EventDependencies.Default(rootModule)
        }
        override val eventManager: EventManager by lazy {
            EventManager(dependencies)
        }
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    eventManager
                },
                onDisable = {
                    HandlerList.unregisterAll(rootModule.bukkitModule.plugin.value)
                }
            )
        }
    }
}
