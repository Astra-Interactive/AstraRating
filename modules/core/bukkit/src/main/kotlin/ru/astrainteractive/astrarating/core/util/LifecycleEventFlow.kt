package ru.astrainteractive.astrarating.core.util

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin

fun <E : LifecycleEvent> JavaPlugin.lifecycleEventFlow(
    configuration: LifecycleEventType.Prioritizable<LifecycleEventOwner, E>
) = channelFlow {
    var isEnabled = true
    val eventHandler: LifecycleEventHandler<E> = LifecycleEventHandler<E> {
        if (!isEnabled) return@LifecycleEventHandler
        launch { send(it) }
    }
    lifecycleManager.registerEventHandler(configuration, eventHandler)

    awaitClose {
        isEnabled = false
    }
}
