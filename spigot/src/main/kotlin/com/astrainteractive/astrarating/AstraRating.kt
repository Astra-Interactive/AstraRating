package com.astrainteractive.astrarating

import CommandManager
import com.astrainteractive.astrarating.modules.BStats
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.DBModule
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.Files
import com.astrainteractive.astrarating.utils.RatingPAPIExpansion
import github.scarsz.discordsrv.DiscordSRV
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.utils.setupWithSpigot


/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {
    companion object {
        lateinit var instance: AstraRating
        val discordSRV: DiscordSRV? by lazy {
            kotlin.runCatching { Bukkit.getPluginManager().getPlugin("DiscordSRV") as DiscordSRV }.getOrNull()
        }
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot("AstraRating")
        instance = this
        reloadPlugin()
        CommandManager()
        BStats.value
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            if (RatingPAPIExpansion.isRegistered) return@let
            RatingPAPIExpansion.register()
        }
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { DBModule.value.closeConnection()}
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        RatingPAPIExpansion.unregister()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        Files.configFile.reload()
        ConfigProvider.reload()
        TranslationProvider.reload()

    }

}


