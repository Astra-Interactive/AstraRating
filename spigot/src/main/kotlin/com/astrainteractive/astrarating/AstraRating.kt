@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astrarating

import CommandManager
import com.astrainteractive.astrarating.modules.impl.CommandsModuleImpl
import com.astrainteractive.astrarating.modules.impl.RootModuleImpl
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent

/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {

    init {
        RootModuleImpl.plugin.initialize(this)
    }
    private val papiModule by RootModuleImpl.papiExpansion

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        reloadPlugin()
        RootModuleImpl.database
        RootModuleImpl.bstats.build()
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            if (papiModule.isRegistered) {
                papiModule.unregister()
            }

            papiModule.register()
        }
        GlobalInventoryClickEvent.onEnable(this)
        RootModuleImpl.eventManager.build()
        CommandManager(this, CommandsModuleImpl)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { RootModuleImpl.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        papiModule.unregister()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        RootModuleImpl.configFileManager.value.reload()
        RootModuleImpl.config.reload()
        RootModuleImpl.translation.reload()
    }
}
