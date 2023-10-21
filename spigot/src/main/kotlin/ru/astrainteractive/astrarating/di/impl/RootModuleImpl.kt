@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.astrarating.di.impl

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.event.GlobalEventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.api.rating.api.impl.RatingDBApiImpl
import ru.astrainteractive.astrarating.api.rating.usecase.InsertUserUseCase
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.RatingPAPIComponent
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

class RootModuleImpl : RootModule {
    // Core
    override val plugin = Lateinit<AstraRating>()

    // Modules
    override val commandsModule: CommandsModule by Provider {
        CommandsModuleImpl(this)
    }
    override val papiModule: PapiModule by Provider {
        PapiModuleImpl(this)
    }
    override val eventModule: EventModule by Provider {
        EventModuleImpl(this)
    }
    override val guiModule: GuiModule by Provider {
        GuiModuleImpl(this)
    }
    // Etc

    override val bstats = Factory {
        val plugin by plugin
        Metrics(plugin, 15801)
    }
    override val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, "config.yml")
    }
    override val papiExpansion = Single {
        runCatching {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                RatingPAPIComponent(papiModule)
            } else {
                error("No PlaceholderAPI ")
            }
        }.onFailure {
            Bukkit.getLogger().severe("[AstraRating] You don't have PAPI installed. Placeholders will not be avaliable")
        }.getOrNull()
    }

    override val scope = Single {
        PluginScope
    }
    override val eventListener = Single {
        GlobalEventListener
    }
    override val dispatchers = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val config = Reloadable {
        ConfigLoader().toClassOrDefault(configFileManager.value.configFile, ::EmpireConfig)
    }
    override val translation = Reloadable {
        val plugin by plugin
        PluginTranslation(plugin)
    }
    override val database = Single {
        val plugin by plugin
        RatingDatabaseFactory(
            dbConnection = when (val mysql = config.value.databaseConnection.mysql) {
                null -> {
                    DBConnection.SQLite("${plugin.dataFolder}${File.separator}data.db")
                }

                else -> {
                    DBConnection.MySql(
                        url = "jdbc:mysql://${mysql.host}:${mysql.port}/${mysql.database}",
                        user = mysql.username,
                        password = mysql.password
                    )
                }
            }
        ).create()
    }

    override val dbApi = Single {
        RatingDBApiImpl(database.value, plugin.value.dataFolder)
    }
    override val cachedApi = Single {
        val scope by scope
        ru.astrainteractive.astrarating.api.rating.api.impl.CachedApiImpl(
            dbApi.value,
            scope
        )
    }
    override val eventManager = Factory {
        EventManager(eventModule)
    }
    override val insertUserUseCase = Single {
        InsertUserUseCase(dbApi.value) {
            null
        }
    }
}
