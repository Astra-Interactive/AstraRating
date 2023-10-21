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
        rootModule.servicesModule.plugin.initialize(this)
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        reloadPlugin()
        rootModule.dbRatingModule.database
        rootModule.servicesModule.bstats.create()
        rootModule.papiModule?.ratingPAPIComponent?.onEnable()
        GlobalInventoryClickEvent.onEnable(this)
        rootModule.servicesModule.eventManager.create()
        CommandManager(rootModule.commandsModule)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { rootModule.dbRatingModule.database.connector.invoke().close() }
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        rootModule.papiModule?.ratingPAPIComponent?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        rootModule.servicesModule.configFileManager.value.reload()
        rootModule.servicesModule.config.reload()
        rootModule.servicesModule.translation.reload()
    }
}
