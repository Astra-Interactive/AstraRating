package ru.astrainteractive.astrarating.di.impl

import org.bukkit.Bukkit
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsDependencies
import ru.astrainteractive.astrarating.command.di.CommandsDependenciesImpl
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.di.ServicesModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.di.GuiModuleImpl
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

class RootModuleImpl : RootModule {

    override val servicesModule: ServicesModule by Single {
        ServicesModuleImpl(this)
    }

    // Modules
    override val commandsDependencies: CommandsDependencies by Provider {
        CommandsDependenciesImpl(this)
    }
    override val guiModule: GuiModule by Provider {
        GuiModuleImpl(this)
    }

    override val dbRatingModule: DBRatingModule by Single {
        val plugin by servicesModule.plugin
        val config by servicesModule.config
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
        val plugin by servicesModule.plugin
        val scope by servicesModule.scope
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
                config = servicesModule.config,
                scope = servicesModule.scope.value
            )
        }.onFailure {
            Bukkit.getLogger().severe("[AstraRating] You don't have PAPI installed. Placeholders will not be avaliable")
        }.getOrNull()
    }

    override val sharedModule: SharedModule by Single {
        SharedModule.Default(
            apiRatingModule = apiRatingModule,
            dispatchers = servicesModule.dispatchers.value,
            coroutineScope = servicesModule.scope.value,
            permissionManager = servicesModule.permissionManager.value,
            empireConfig = servicesModule.config
        )
    }
}
