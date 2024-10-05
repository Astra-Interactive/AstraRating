package ru.astrainteractive.astrarating.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate

interface BukkitModule {
    val lifecycle: Lifecycle

    // Core
    val plugin: LifecyclePlugin
    val kyoriComponentSerializer: Krate<KyoriComponentSerializer>

    // Services
    val bstats: () -> Metrics
    val eventListener: EventListener
    val inventoryClickEvent: DefaultInventoryClickEvent

    class Default(override val plugin: LifecyclePlugin) : BukkitModule {

        override val inventoryClickEvent by lazy {
            DefaultInventoryClickEvent()
        }

        override val kyoriComponentSerializer = DefaultStateFlowMutableKrate<KyoriComponentSerializer>(
            factory = { KyoriComponentSerializer.Legacy },
            loader = { KyoriComponentSerializer.Legacy }
        )

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
