package ru.astrainteractive.astrarating.core.di

import com.google.common.cache.CacheBuilder
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent

class BukkitModule(
    val plugin: LifecyclePlugin,
) {

    val inventoryClickEvent by lazy {
        CacheBuilder.newBuilder()
        DefaultInventoryClickEvent()
    }

    val bstats = {
        @Suppress("MagicNumber")
        Metrics(plugin, 15801)
    }

    val eventListener by lazy {
        EventListener.Default()
    }

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                eventListener.onEnable(plugin)
                inventoryClickEvent.onEnable(plugin)
                bstats.invoke()
            },
            onDisable = {
                eventListener.onDisable()
                inventoryClickEvent.onDisable()
            }
        )
    }
}
