package ru.astrainteractive.astrarating.di.impl

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.changerating.data.BukkitPlatformBridge
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

class RootModuleImpl : RootModule {

    override val bukkitModule: BukkitModule by Single {
        BukkitModule.Default()
    }

    override val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = bukkitModule.plugin.value.dataFolder,
            dispatchers = DefaultBukkitDispatchers(bukkitModule.plugin.value)
        )
    }

    override val dbRatingModule: DBRatingModule by Single {
        val plugin by bukkitModule.plugin
        val config by coreModule.config
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
        val scope by coreModule.scope
        ApiRatingModule.Default(
            database = dbRatingModule.database,
            coroutineScope = scope,
            plugin.dataFolder
        )
    }

    override val papiModule: PapiModule? by Single {
        PapiModule.Default(
            cachedApi = apiRatingModule.cachedApi,
            config = coreModule.config,
            scope = coreModule.scope.value
        )
    }

    override val sharedModule: SharedModule by Single {
        SharedModule.Default(
            apiRatingModule = apiRatingModule,
            dispatchers = coreModule.dispatchers,
            coroutineScope = coreModule.scope.value,
            empireConfig = coreModule.config,
            platformBridge = {
                BukkitPlatformBridge(
                    minTimeOnServer = {
                        coreModule.config.value.minTimeOnServer
                    }
                )
            }
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
