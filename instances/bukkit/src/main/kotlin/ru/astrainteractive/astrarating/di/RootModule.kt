package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

interface RootModule {
    val lifecycle: Lifecycle

    val bukkitModule: BukkitModule

    val coreModule: CoreModule

    val dbRatingModule: DBRatingModule

    val apiRatingModule: ApiRatingModule

    val papiModule: PapiModule

    val sharedModule: SharedModule

    val guiModule: GuiModule

    val eventModule: EventModule

    val commandsModule: CommandsModule

    class Default(plugin: LifecyclePlugin) : RootModule {

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
                stringFormat = coreModule.yamlStringFormat,
                dataFolder = bukkitModule.plugin.dataFolder,
            )
        }

        override val apiRatingModule: ApiRatingModule by lazy {
            ApiRatingModule.Default(
                databaseFlow = dbRatingModule.databaseFlow,
                coroutineScope = coreModule.scope,
                isDebugProvider = { coreModule.config.cachedValue.debug }
            )
        }

        override val papiModule: PapiModule by lazy {
            PapiModule.Default(
                cachedApi = apiRatingModule.cachedApi,
                scope = coreModule.scope,
                dataFolder = bukkitModule.plugin.dataFolder,
                yamlStringFormat = coreModule.yamlStringFormat
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
                kyoriKrate = bukkitModule.kyoriComponentSerializer,
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
        private val lifecycles: List<Lifecycle>
            get() = listOfNotNull(
                coreModule.lifecycle,
                bukkitModule.lifecycle,
                dbRatingModule.lifecycle,
                apiRatingModule.lifecycle,
                sharedModule.lifecycle,
                commandsModule.lifecycle,
                eventModule.lifecycle,
                papiModule.lifecycle
            )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = { lifecycles.forEach(Lifecycle::onEnable) },
            onReload = { lifecycles.forEach(Lifecycle::onReload) },
            onDisable = { lifecycles.forEach(Lifecycle::onDisable) },
        )
    }
}
