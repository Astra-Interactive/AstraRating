package ru.astrainteractive.astrarating.di.impl

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
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
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

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
        DBRatingModule.Default(
            coreModule = coreModule,
            dataFolder = bukkitModule.plugin.value.dataFolder,
        )
    }

    override val apiRatingModule: ApiRatingModule by Provider {
        val scope by coreModule.scope
        ApiRatingModule.Default(
            database = dbRatingModule.database,
            coroutineScope = scope,
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
        )
    }

    override val guiModule: GuiModule by lazy {
        GuiModule.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = bukkitModule.kyoriComponentSerializer.value,
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
