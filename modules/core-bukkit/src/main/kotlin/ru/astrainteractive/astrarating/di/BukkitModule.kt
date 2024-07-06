package ru.astrainteractive.astrarating.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface BukkitModule {
    val lifecycle: Lifecycle

    // Core
    val plugin: Lateinit<LifecyclePlugin>
    val inventoryClickEvent: Single<DefaultInventoryClickEvent>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>

    // Services
    val bstats: Factory<Metrics>
    val eventListener: Single<EventListener>

    class Default : BukkitModule {
        override val plugin = Lateinit<LifecyclePlugin>()

        override val inventoryClickEvent: Single<DefaultInventoryClickEvent> = Single {
            DefaultInventoryClickEvent()
        }

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }

        override val bstats = Factory {
            val plugin by plugin
            Metrics(plugin, 15801)
        }

        override val eventListener: Single<EventListener> = Single {
            object : EventListener {}
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    eventListener.value.onEnable(plugin.value)
                    inventoryClickEvent.value.onEnable(plugin.value)
                    bstats.create()
                },
                onDisable = {
                    eventListener.value.onDisable()
                    inventoryClickEvent.value.onDisable()
                }
            )
        }
    }
}
