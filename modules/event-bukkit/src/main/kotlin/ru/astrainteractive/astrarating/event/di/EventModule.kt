package ru.astrainteractive.astrarating.event.di

import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.data.dao.di.RatingDaoModule
import ru.astrainteractive.astrarating.event.kill.KillEventListener
import ru.astrainteractive.astrarating.event.login.LoginEvent
import ru.astrainteractive.astrarating.event.login.RatingCacheWarmUp

class EventModule(
    coreModule: CoreModule,
    ratingDaoModule: RatingDaoModule,
    bukkitModule: BukkitModule
) {
    private val killEvent by lazy {
        KillEventListener(
            configKrate = coreModule.configKrate,
            translationKrate = coreModule.translationKrate,
            ratingDao = ratingDaoModule.ratingDao,
            scope = coreModule.ioScope,
            dispatchers = coreModule.dispatchers
        )
    }

    @Suppress("UnusedPrivateProperty")
    private val loginEvent = LoginEvent(
        plugin = bukkitModule.plugin,
        ratingCachedDao = ratingDaoModule.ratingCachedDao,
        mainScope = coreModule.mainScope,
        ioScope = coreModule.ioScope
    )

    private val ratingCacheWarmUp by lazy {
        RatingCacheWarmUp(
            plugin = bukkitModule.plugin,
            ratingCachedDao = ratingDaoModule.ratingCachedDao,
            ioScope = coreModule.ioScope
        )
    }

    private val events: List<EventListener>
        get() = listOf(killEvent)

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                events.forEach { event -> event.onEnable(bukkitModule.plugin) }
                ratingCacheWarmUp.onEnable()
            },
            onDisable = {
                events.forEach(EventListener::onDisable)
                HandlerList.unregisterAll(bukkitModule.plugin)
            }
        )
    }
}
