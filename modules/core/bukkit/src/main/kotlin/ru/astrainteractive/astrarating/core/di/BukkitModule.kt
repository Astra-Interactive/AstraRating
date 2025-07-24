package ru.astrainteractive.astrarating.core.di

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineScope
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astrarating.core.util.PaperCommandRegistrarContext
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate

class BukkitModule(
    val plugin: LifecyclePlugin,
    mainScope: CoroutineScope
) {

    val inventoryClickEvent by lazy {
        CacheBuilder.newBuilder()
        DefaultInventoryClickEvent()
    }

    val kyoriKrate = DefaultMutableKrate<KyoriComponentSerializer>(
        factory = { KyoriComponentSerializer.Legacy },
        loader = { KyoriComponentSerializer.Legacy }
    ).asCachedKrate()

    val bstats = {
        Metrics(plugin, 15801)
    }

    val eventListener by lazy {
        EventListener.Default()
    }
    val commandRegistrarContext = PaperCommandRegistrarContext(
        mainScope = mainScope,
        plugin = plugin
    )

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
