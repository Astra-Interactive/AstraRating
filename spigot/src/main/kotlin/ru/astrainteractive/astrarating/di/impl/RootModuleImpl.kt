package ru.astrainteractive.astrarating.di.impl

import org.bukkit.Bukkit
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

class RootModuleImpl : RootModule {

    override val bukkitModule: BukkitModule by Single {
        BukkitModuleImpl()
    }

    // Modules

    override val dbRatingModule: DBRatingModule by Single {
        val plugin by bukkitModule.plugin
        val config by bukkitModule.config
        DBRatingModule.Default(
            connection = when (val mysql = config.databaseConnection.mysql) {
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
        )
    }
    override val apiRatingModule: ApiRatingModule by Provider {
        val plugin by bukkitModule.plugin
        val scope by bukkitModule.scope
        ApiRatingModule.Default(
            database = dbRatingModule.database,
            coroutineScope = scope,
            plugin.dataFolder
        )
    }

    override val papiModule: PapiModule? by Single {
        runCatching {
            PapiModule.Default(
                cachedApi = apiRatingModule.cachedApi,
                config = bukkitModule.config,
                scope = bukkitModule.scope.value
            )
        }.onFailure {
            Bukkit.getLogger().severe("[AstraRating] You don't have PAPI installed. Placeholders will not be avaliable")
        }.getOrNull()
    }

    override val sharedModule: SharedModule by Single {
        SharedModule.Default(
            apiRatingModule = apiRatingModule,
            dispatchers = bukkitModule.dispatchers.value,
            coroutineScope = bukkitModule.scope.value,
            empireConfig = bukkitModule.config,
            platformBridge = bukkitModule.platformBridge
        )
    }

    override val guiModule: GuiModule by lazy {
        GuiModule.Default(this)
    }

    override val eventModule: EventModule by lazy {
        EventModule.Default(this)
    }
    override val commandsModule: CommandsModule by lazy {
        CommandsModule.Default(this)
    }
}
