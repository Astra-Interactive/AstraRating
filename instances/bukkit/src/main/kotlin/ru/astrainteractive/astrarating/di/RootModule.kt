package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.command.PaperMultiplatformCommands
import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.coroutines.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.server.bridge.BukkitPlatformServer
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.data.exposed.di.ExposedModule
import ru.astrainteractive.astrarating.data.exposed.rating.di.RatingStorageModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.gui.di.GuiBukkitModule
import ru.astrainteractive.astrarating.feature.gui.di.GuiModule
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

class RootModule(plugin: LifecyclePlugin) {
    private val coreModule: CoreModule by lazy {
        CoreModule(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin),
            multiplatformCommand = MultiplatformCommand(PaperMultiplatformCommands()),
        )
    }
    private val bukkitModule: BukkitModule by lazy {
        BukkitModule(plugin)
    }

    private val exposedModule: ExposedModule by lazy {
        ExposedModule(
            stringFormat = coreModule.yamlStringFormat,
            dataFolder = bukkitModule.plugin.dataFolder,
        )
    }

    private val ratingStorageModule: RatingStorageModule by lazy {
        RatingStorageModule(
            databaseFlow = exposedModule.databaseFlow,
            coroutineScope = coreModule.ioScope,
            isDebugProvider = { coreModule.configKrate.cachedValue.debug }
        )
    }

    private val papiModule: PapiModule by lazy {
        PapiModule(
            ratingCache = ratingStorageModule.ratingCache,
            dataFolder = bukkitModule.plugin.dataFolder,
            yamlStringFormat = coreModule.yamlStringFormat
        )
    }

    private val ratingChangeModule: RatingChangeModule by lazy {
        RatingChangeModule(
            dispatchers = coreModule.dispatchers,
            astraRatingConfigKrate = coreModule.configKrate,
            dbApi = ratingStorageModule.ratingRepository
        )
    }

    private val ratingPlayerModule: RatingPlayerModule by lazy {
        RatingPlayerModule(
            ratingRepository = ratingStorageModule.ratingRepository,
            dispatchers = coreModule.dispatchers,
        )
    }

    private val ratingPlayersModule: RatingPlayersModule by lazy {
        RatingPlayersModule(
            ratingRepository = ratingStorageModule.ratingRepository,
            dispatchers = coreModule.dispatchers,
            coroutineScope = coreModule.ioScope
        )
    }

    private val guiModule: GuiModule by lazy {
        GuiBukkitModule(
            coreModule = coreModule,
            ratingPlayerModule = ratingPlayerModule,
            ratingPlayersModule = ratingPlayersModule
        )
    }

    private val eventModule: EventModule by lazy {
        EventModule(
            coreModule = coreModule,
            ratingRepository = ratingStorageModule.ratingRepository,
            ratingCache = ratingStorageModule.ratingCache,
            bukkitModule = bukkitModule
        )
    }

    private val commandsModule: CommandsModule by lazy {
        CommandsModule(
            coreModule = coreModule,
            guiModule = guiModule,
            ratingChangeModule = ratingChangeModule,
            commandRegistrarContext = PaperCommandRegistrarContext(
                mainScope = coreModule.unconfinedScope,
                plugin = plugin
            ),
            lifecyclePlugin = plugin,
            platformServer = BukkitPlatformServer()
        )
    }
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            coreModule.lifecycle,
            bukkitModule.lifecycle,
            exposedModule.lifecycle,
            ratingStorageModule.lifecycle,
            ratingPlayersModule.lifecycle,
            commandsModule.lifecycle,
            eventModule.lifecycle,
            papiModule.lifecycle,
        )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = { lifecycles.forEach(Lifecycle::onEnable) },
        onReload = { lifecycles.forEach(Lifecycle::onReload) },
        onDisable = { lifecycles.forEach(Lifecycle::onDisable) },
    )
}
