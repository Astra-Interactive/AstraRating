package ru.astrainteractive.astrarating.event.di

import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.event.kill.KillEventListener

interface EventModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        apiRatingModule: ApiRatingModule,
        bukkitModule: BukkitModule
    ) : EventModule {
        private val dependencies by lazy {
            EventDependencies.Default(
                coreModule = coreModule,
                apiRatingModule = apiRatingModule,
                bukkitModule = bukkitModule
            )
        }
        private val killEvent by lazy {
            KillEventListener(dependencies)
        }
        private val events: List<EventListener>
            get() = listOf(killEvent)
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    events.forEach { event -> event.onEnable(bukkitModule.plugin) }
                },
                onDisable = {
                    events.forEach(EventListener::onDisable)
                    HandlerList.unregisterAll(bukkitModule.plugin)
                }
            )
        }
    }
}
