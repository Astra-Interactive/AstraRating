package ru.astrainteractive.astrarating.event.di

import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.event.kill.KillEventListener

class EventModule(
    coreModule: CoreModule,
    apiRatingModule: ApiRatingModule,
    bukkitModule: BukkitModule
) {
    private val killEvent by lazy {
        KillEventListener(
            configKrate = coreModule.configKrate,
            translationKrate = coreModule.translationKrate,
            kyoriKrate = bukkitModule.kyoriKrate,
            ratingDao = apiRatingModule.ratingDao,
            scope = coreModule.scope,
            dispatchers = coreModule.dispatchers
        )
    }

    private val events: List<EventListener>
        get() = listOf(killEvent)

    val lifecycle: Lifecycle by lazy {
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
