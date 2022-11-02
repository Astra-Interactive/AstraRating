package com.astrainteractive.astrarating

import CommandManager
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.ServerVersion
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.utils.RatingPAPIExpansion
import com.astrainteractive.astrarating.modules.BStats
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.modules.DBModule
import com.astrainteractive.astrarating.utils.Files
import github.scarsz.discordsrv.DiscordSRV
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.events.DSLEvent


/**
 * Initial class for your plugin
 */
class AstraRating : JavaPlugin() {
    companion object {
        lateinit var instance: AstraRating
        var discordSRV: DiscordSRV? = null
    }

    init {
        instance = this
        AstraLibs.rememberPlugin(this)
        discordSRV = catching { Bukkit.getPluginManager().getPlugin("DiscordSRV") as DiscordSRV }
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        Logger.prefix = "AstraTemplate"
        reloadPlugin()
        CommandManager()
        BStats.value
        if (ServerVersion.version == ServerVersion.UNMAINTAINED)
            Logger.warn("Your server version is not maintained and might be not fully functional!", "AstraTemplate")
        else
            Logger.log(
                "Your server version is: ${ServerVersion.getServerVersion()}. This version is supported!",
                "AstraTemplate"
            )
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            if (RatingPAPIExpansion.isRegistered) return@let
            RatingPAPIExpansion.register()
        }
        DSLEvent.event(InventoryClickEvent::class.java){ e->
            println(e.inventory.holder)
        }
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        runBlocking { DBModule.value.closeConnection()}
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
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


