package ru.astrainteractive.astrarating.core.util

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import org.bukkit.plugin.java.JavaPlugin

interface CommandRegistrarContext<T> {
    fun registerWhenReady(node: LiteralCommandNode<T>)
}

class PaperCommandRegistrarContext(
    private val mainScope: CoroutineScope,
    private val plugin: JavaPlugin
) : CommandRegistrarContext<CommandSourceStack> {
    val commandsRegistrarFlow = plugin.lifecycleEventFlow(LifecycleEvents.COMMANDS)
        .shareIn(mainScope, SharingStarted.Eagerly, 1)

    override fun registerWhenReady(node: LiteralCommandNode<CommandSourceStack>) {
        commandsRegistrarFlow
            .mapNotNull { it?.registrar() }
            .onEach { it.register(node) }
            .launchIn(mainScope)
    }
}
