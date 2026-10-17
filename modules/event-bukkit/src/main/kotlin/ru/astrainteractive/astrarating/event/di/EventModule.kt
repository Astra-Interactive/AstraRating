package ru.astrainteractive.astrarating.event.di

import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.event.kill.event.KillEventListener
import ru.astrainteractive.astrarating.event.login.event.LoginEvent

class EventModule(
    coreModule: CoreModule,
    ratingRepository: RatingRepository,
    ratingCache: RatingCache,
    bukkitModule: BukkitModule
) {
    private val killEvent by lazy {
        KillEventListener(
            configKrate = coreModule.configKrate,
            translationKrate = coreModule.translationKrate,
            ratingRepository = ratingRepository,
            scope = coreModule.ioScope,
            dispatchers = coreModule.dispatchers
        )
    }

    @Suppress("UnusedPrivateProperty")
    private val loginEvent = LoginEvent(
        plugin = bukkitModule.plugin,
        ratingCache = ratingCache,
        mainScope = coreModule.mainScope,
        ioScope = coreModule.ioScope
    )

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
