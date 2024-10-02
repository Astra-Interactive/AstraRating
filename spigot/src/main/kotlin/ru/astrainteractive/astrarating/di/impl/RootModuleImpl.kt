package ru.astrainteractive.astrarating.di.impl

import kotlinx.coroutines.flow.map
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

class RootModuleImpl(plugin: LifecyclePlugin) : RootModule {

    override val bukkitModule: BukkitModule by lazy {
        BukkitModule.Default(plugin)
    }

    override val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = bukkitModule.plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(bukkitModule.plugin)
        )
    }

    override val dbRatingModule: DBRatingModule by lazy {
        DBRatingModule.Default(
            dataFolder = bukkitModule.plugin.dataFolder,
            dbConfigurationFlow = coreModule.config
                .cachedStateFlow
                .map { it.databaseConnection }
        )
    }

    override val apiRatingModule: ApiRatingModule by lazy {
        ApiRatingModule.Default(
            databaseFlow = dbRatingModule.databaseFlow,
            coroutineScope = coreModule.scope,
            isDebugProvider = { coreModule.config.cachedValue.debug }
        )
    }

    override val papiModule: PapiModule? by lazy {
        PapiModule.Default(
            cachedApi = apiRatingModule.cachedApi,
            configKrate = coreModule.config,
            scope = coreModule.scope
        )
    }

    override val sharedModule: SharedModule by lazy {
        SharedModule.Default(
            apiRatingModule = apiRatingModule,
            dispatchers = coreModule.dispatchers,
            coroutineScope = coreModule.scope,
            empireConfigKrate = coreModule.config,
        )
    }

    override val guiModule: GuiModule by lazy {
        GuiModule.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = bukkitModule.kyoriComponentSerializer.cachedValue,
            sharedModule = sharedModule
        )
    }

    override val eventModule: EventModule by lazy {
        EventModule.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            bukkitModule = bukkitModule
        )
    }

    override val commandsModule: CommandsModule by lazy {
        CommandsModule.Default(
            sharedModule = sharedModule,
            bukkitModule = bukkitModule,
            coreModule = coreModule,
            guiModule = guiModule
        )
    }
}
