@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.astrarating

import CommandManager
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.event.GlobalEventListener
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent
import ru.astrainteractive.astrarating.di.impl.RootModuleImpl

/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {
    private val rootModule = RootModuleImpl()

    init {
        rootModule.plugin.initialize(this)
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        reloadPlugin()
        rootModule.database
        rootModule.bstats.create()
        rootModule.papiExpansion.value?.onEnable()
        GlobalInventoryClickEvent.onEnable(this)
        rootModule.eventManager.create()
        CommandManager(rootModule.commandsModule)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { rootModule.database.value.connector.invoke().close() }
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        rootModule.papiExpansion.value?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        rootModule.configFileManager.value.reload()
        rootModule.config.reload()
        rootModule.translation.reload()
    }
}
