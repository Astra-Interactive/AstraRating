package com.astrainteractive.astrarating

import CommandManager
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.ServerVersion
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.api.RatingPAPIExpansion
import com.astrainteractive.astrarating.sqldatabase.SQLDatabase
import com.astrainteractive.astrarating.utils.PluginTranslation
import com.astrainteractive.astrarating.utils._Files
import com.astrainteractive.astrarating.utils.EmpireConfig
import github.scarsz.discordsrv.DiscordSRV
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.PluginScope


class BStats private constructor(private val id:Int) {
    private val metrics = Metrics(AstraRating.instance,id)
    companion object {
        private var instance: BStats? = null
        fun create() {
            if (instance != null) return
            instance = BStats(15801)
        }

    }
}
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
     * Command manager for your commands.
     *
     * You can create multiple managers.
     */
    private lateinit var commandManager: CommandManager

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        Logger.prefix = "AstraTemplate"
        PluginTranslation()
        _Files()
        commandManager = CommandManager()
        BStats.create()
        EmpireConfig.create()
        PluginScope.launch { SQLDatabase().onEnable() }
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
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        PluginScope.launch { SQLDatabase.instance?.close() }
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


