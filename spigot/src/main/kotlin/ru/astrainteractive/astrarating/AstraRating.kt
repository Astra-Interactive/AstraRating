package ru.astrainteractive.astrarating

import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astrarating.di.impl.RootModuleImpl

/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {
    private val rootModule = RootModuleImpl()

    init {
        rootModule.bukkitModule.plugin.initialize(this)
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        reloadPlugin()
        rootModule.dbRatingModule.database
        rootModule.bukkitModule.bstats.create()
        rootModule.papiModule?.ratingPAPILifecycle?.onEnable()
        rootModule.bukkitModule.eventListener.value.onEnable(this)
        rootModule.bukkitModule.inventoryClickEvent.value.onEnable(this)
        rootModule.eventModule.eventManager
        rootModule.commandsModule.commandManager
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { rootModule.dbRatingModule.database.connector.invoke().close() }
        HandlerList.unregisterAll(this)
        rootModule.bukkitModule.eventListener.value.onDisable()
        rootModule.bukkitModule.inventoryClickEvent.value.onDisable()
        rootModule.papiModule?.ratingPAPILifecycle?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        rootModule.bukkitModule.config.reload()
        rootModule.bukkitModule.translation.reload()
    }
}
