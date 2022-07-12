package com.astrainteractive.astrarating

import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.ServerVersion
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.astrarating.events.EventHandler
import com.astrainteractive.astrarating.sqldatabase.DatabaseCore
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.utils.PluginTranslation
import com.astrainteractive.astrarating.utils._Files
import com.astrainteractive.astrarating.utils.EmpireConfig
import github.scarsz.discordsrv.DiscordSRV
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

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
        discordSRV = catching { Bukkit.getPluginManager().getPlugin("DiscordSRV") as DiscordSRV }
    }

    /**
     * Class for handling all of your events
     */
    private lateinit var eventHandler: EventHandler

    /**
     * Command manager for your commands.
     *
     * You can create multiple managers.
     */
    private lateinit var commandManager: CommandManager

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraTemplate"
        PluginTranslation()
        _Files()
        eventHandler = EventHandler()
        commandManager = CommandManager()
        EmpireConfig.create()
        runBlocking { SQLDatabase.onEnable() }
        if (ServerVersion.version == ServerVersion.UNMAINTAINED)
            Logger.warn("Your server version is not maintained and might be not fully functional!", "AstraTemplate")
        else
            Logger.log(
                "Your server version is: ${ServerVersion.getServerVersion()}. This version is supported!",
                "AstraTemplate"
            )
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        eventHandler.onDisable()
        runBlocking { SQLDatabase.close() }
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        onDisable()
        onEnable()
    }

}


