package com.astrainteractive.astrarating

import CommandManager
import com.astrainteractive.astrarating.modules.*
import com.astrainteractive.astrarating.plugin.Files
import com.astrainteractive.astrarating.integrations.RatingPAPIExpansion
import github.scarsz.discordsrv.DiscordSRV
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.menu.event.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.Singleton
import ru.astrainteractive.astralibs.utils.setupWithSpigot


/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {
    companion object : Singleton<AstraRating>() {
        val discordSRV: DiscordSRV? by lazy {
            kotlin.runCatching { Bukkit.getPluginManager().getPlugin("DiscordSRV") as DiscordSRV }.getOrNull()
        }
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        instance = this
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot("AstraRating", this)
        reloadPlugin()
        CommandManager()
        ServiceLocator.database
        ServiceLocator.bstats.value
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            if (RatingPAPIExpansion.isRegistered)
                RatingPAPIExpansion.unregister()

            RatingPAPIExpansion.register()
        }
        SharedInventoryClickEvent.onEnable(this)
        Bukkit.getPluginManager().getPlugin("AstraLibs")
        ServiceLocator.eventManager.value

    }


    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { ServiceLocator.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        RatingPAPIExpansion.unregister()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        Files.configFile.reload()
        ServiceLocator.config.reload()
        ServiceLocator.translation.reload()

    }


}




