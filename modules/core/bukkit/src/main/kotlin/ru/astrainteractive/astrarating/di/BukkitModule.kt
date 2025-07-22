package ru.astrainteractive.astrarating.di

import com.google.common.cache.CacheBuilder
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate

interface BukkitModule {
    val lifecycle: Lifecycle

    // Core
    val plugin: LifecyclePlugin
    val kyoriComponentSerializer: CachedKrate<KyoriComponentSerializer>

    // Services
    val bstats: () -> Metrics
    val eventListener: EventListener
    val inventoryClickEvent: DefaultInventoryClickEvent

    class Default(override val plugin: LifecyclePlugin) : BukkitModule {

        override val inventoryClickEvent by lazy {
            CacheBuilder.newBuilder()
            DefaultInventoryClickEvent()
        }

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            factory = { KyoriComponentSerializer.Legacy },
            loader = { KyoriComponentSerializer.Legacy }
        ).asCachedKrate()

        override val bstats = {
            Metrics(plugin, 15801)
        }

        override val eventListener by lazy {
            EventListener.Default()
        }

        override val lifecycle: Lifecycle by lazy {
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
}
