package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astralibs.coroutines.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.core.gui.di.GuiBukkitModule
import ru.astrainteractive.astrarating.data.dao.di.RatingDaoModule
import ru.astrainteractive.astrarating.data.exposed.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

class RootModule(plugin: LifecyclePlugin) {
    private val coreModule: CoreModule by lazy {
        CoreModule(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin)
        )
    }
    private val bukkitModule: BukkitModule by lazy {
        BukkitModule(plugin, coreModule.mainScope)
    }

    private val dbRatingModule: DBRatingModule by lazy {
        DBRatingModule(
            stringFormat = coreModule.yamlStringFormat,
            dataFolder = bukkitModule.plugin.dataFolder,
        )
    }

    private val ratingDaoModule: RatingDaoModule by lazy {
        RatingDaoModule(
            databaseFlow = dbRatingModule.databaseFlow,
            coroutineScope = coreModule.ioScope,
            isDebugProvider = { coreModule.configKrate.cachedValue.debug }
        )
    }

    private val papiModule: PapiModule by lazy {
        PapiModule(
            ratingCachedDao = ratingDaoModule.ratingCachedDao,
            scope = coreModule.ioScope,
            dataFolder = bukkitModule.plugin.dataFolder,
            yamlStringFormat = coreModule.yamlStringFormat
        )
    }

    private val ratingChangeModule: RatingChangeModule by lazy {
        RatingChangeModule(
            dispatchers = coreModule.dispatchers,
            astraRatingConfigKrate = coreModule.configKrate,
            dbApi = ratingDaoModule.ratingDao
        )
    }

    private val ratingPlayerModule: RatingPlayerModule by lazy {
        RatingPlayerModule(
            ratingDaoModule = ratingDaoModule,
            dispatchers = coreModule.dispatchers,
        )
    }

    private val ratingPlayersModule: RatingPlayersModule by lazy {
        RatingPlayersModule(
            ratingDaoModule = ratingDaoModule,
            dispatchers = coreModule.dispatchers,
            coroutineScope = coreModule.ioScope
        )
    }

    private val guiBukkitModule: GuiBukkitModule by lazy {
        GuiBukkitModule(
            coreModule = coreModule,
            translationContext = bukkitModule.kyoriKrate,
            ratingPlayerModule = ratingPlayerModule,
            ratingPlayersModule = ratingPlayersModule
        )
    }

    private val eventModule: EventModule by lazy {
        EventModule(
            coreModule = coreModule,
            ratingDaoModule = ratingDaoModule,
            bukkitModule = bukkitModule
        )
    }

    private val commandsModule: CommandsModule by lazy {
        CommandsModule(
            bukkitModule = bukkitModule,
            coreModule = coreModule,
            guiBukkitModule = guiBukkitModule,
            ratingChangeModule = ratingChangeModule
        )
    }
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            coreModule.lifecycle,
            bukkitModule.lifecycle,
            dbRatingModule.lifecycle,
            ratingDaoModule.lifecycle,
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
